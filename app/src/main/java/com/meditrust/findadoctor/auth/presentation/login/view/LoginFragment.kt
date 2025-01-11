package com.meditrust.findadoctor.auth.presentation.login.view

import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.translation.Translator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.data.model.ChatGPTRequest
import com.meditrust.findadoctor.core.data.model.ChatGPTResponse
import com.meditrust.findadoctor.core.data.model.Message
import com.meditrust.findadoctor.core.data.source.remote.RetrofitInstance
import com.meditrust.findadoctor.core.util.ChatGptUtils
import com.meditrust.findadoctor.core.util.NoUnderlineClickableSpan
import com.meditrust.findadoctor.core.util.PersistenceUtil
import com.meditrust.findadoctor.core.util.TextViewUtils
import com.meditrust.findadoctor.core.util.UserRoles
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var persistenceUtil: PersistenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        persistenceUtil = PersistenceUtil(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun setupUIComponents() {
        setupSignUpText()
        setupPrivacyPolicy()
        binding.progressBar.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarNavigation()
        //   setupFirebaseAuth()
        setupUIComponents()
        setupValidation()
        configureButtonListeners(view)
        underlineText()
    }

    private fun navigateToHomeScreenBasedOnUserRole(userRole: String) {
        if (isAdded) {  // Check if fragment is added before navigation
            when (userRole) {
                UserRoles.ADMIN -> {
                    persistenceUtil.setOnboardingCompleted(true)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }

                UserRoles.DOCTOR -> {
                    persistenceUtil.setOnboardingCompleted(true)
                    if (persistenceUtil.isProfileCompleted()) {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_doctorProfileInfoFragment)
                    }
                }

                UserRoles.PATIENT -> {
                    persistenceUtil.setOnboardingCompleted(true)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }

                else -> Log.d(TAG, "Unknown user role: $userRole")
            }
        } else {
            Log.e(TAG, "Fragment is not added. Skipping navigation.")
        }
    }

    private fun setupToolbarNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun configureButtonListeners(view: View) {
        loginListener(view)
        forgotPasswordListener(view)
        verifyAccountListener(view)
    }

    private fun verifyAccountListener(view: View) {
        binding.tvVerifyAccount.setOnClickListener {
            hideKeyboard(view)
            findNavController().navigate(R.id.action_loginFragment_to_resendVerificationBottomSheet)
        }
    }

    private fun forgotPasswordListener(view: View) {
        binding.tvForgotPassword.setOnClickListener {
            hideKeyboard(view)
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordBottomSheet)
        }
    }

    private fun loginListener(view: View) {
        //azharulislamtech@gmail.com->azharul121839
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.isEnabled = false
            hideKeyboard(view)
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()

            if (!email.isEmpty() && !password.isEmpty()) {
                binding.progressBar.show()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        binding.progressBar.hide()
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail: success")
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            verifyAccount(currentUser)
                            clearAllFields()
                            binding.btnContinue.isEnabled = true
                        } else {
                            binding.tvSignInErrors.text =
                                "signInWithEmail: failure ${task.exception}"
                            binding.btnContinue.isEnabled = true
                        }
                    }.addOnFailureListener { e ->
                        binding.tvSignInErrors.text = "Authentication Failed: ${e.message}"
                        binding.btnContinue.isEnabled = true
                    }

            } else {
                binding.tvSignInErrors.text = "You didn't fill in all the fields."
                binding.btnContinue.isEnabled = true
            }

            //  checkChatGPTResponse(email)


        }
    }


    private fun checkChatGPTResponse(userInput: String) {
        if (userInput.isNotEmpty()) {
            val request = ChatGPTRequest(
                model = ChatGptUtils.MODEL,
                messages = listOf(Message(role = "user", content = userInput))
            )

            RetrofitInstance.api.getChatResponse(request)
                .enqueue(object : Callback<ChatGPTResponse> {
                    override fun onResponse(
                        call: Call<ChatGPTResponse>,
                        response: Response<ChatGPTResponse>
                    ) {
                        if (response.isSuccessful) {
                            val reply = response.body()?.choices?.firstOrNull()?.message?.content
                            binding.tvSignInErrors.text = reply ?: "No response"
                        } else {
                            binding.tvSignInErrors.text = "Error: ${response.errorBody()?.string()}"
                        }
                    }

                    override fun onFailure(call: Call<ChatGPTResponse>, t: Throwable) {
                        binding.tvSignInErrors.text = "Failure: ${t.message}"
                    }
                })
        }
    }

    private fun verifyAccount(user: FirebaseUser?) {

        if (user != null) {
            Toast.makeText(context, "Authenticated with: ${user.displayName}", Toast.LENGTH_SHORT)
                .show()
            val userRole = user.displayName.takeIf { !it.isNullOrBlank() } ?: "guest"
            if (user.isEmailVerified) {
                Log.d(TAG, "onAuthStateChanged:signed_in:${user.uid}")
                Toast.makeText(context, "Authenticated with: ${user.email}", Toast.LENGTH_SHORT)
                    .show()
                navigateToHomeScreenBasedOnUserRole(userRole)
            } else {
                Toast.makeText(
                    context,
                    "Email is not Verified\nCheck your Inbox",
                    Toast.LENGTH_SHORT
                ).show()
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    private fun setupValidation() {
        with(binding) {
            val textWatcherEmail = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    tvSignInErrors.text = ""
                }
            }
            val textWatcherPassword = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    tvSignInErrors.text = ""
                }
            }
            inputEmail.addTextChangedListener(textWatcherEmail)
            inputPassword.addTextChangedListener(textWatcherPassword)
        }
    }

    private fun clearAllFields() {
        with(binding) {
            inputEmail.text?.clear()
            inputEmail.clearFocus()

            inputPassword.text?.clear()
            inputPassword.clearFocus()
            tvSignInErrors.text = ""
        }
    }

    private fun underlineText() {
        binding.tvForgotPassword.paintFlags =
            binding.tvForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvVerifyAccount.paintFlags =
            binding.tvVerifyAccount.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setupSignUpText() {
        val text = getString(R.string.sign_up_prompt)
        val signUpText = getString(R.string.sign_up)
        val spannableString = SpannableString(text)

        val start = text.indexOf(signUpText)
        val end = start + signUpText.length

        val clickableSpan = object : NoUnderlineClickableSpan() {
            override fun onClick(widget: View) {
                // Define your click action here
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }
        }
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSignUp.text = spannableString
        binding.tvSignUp.movementMethod = LinkMovementMethod.getInstance()  // Enable click handling
    }

    private fun setupPrivacyPolicy() {
        val policyText = "By signing in, you agree to our Terms of Use and Privacy Policy."
        val clickableParts = mapOf(
            "Terms of Use" to "https://healthengine.com.au/terms_text",
            "Privacy Policy" to "https://healthengine.com.au/privacy_text"
        )
        TextViewUtils.setupClickableTextView(
            requireContext(),
            binding.tvTerms,
            policyText,
            clickableParts
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function to transliterate an English sentence to Bangla phonetic representation
    fun transliterateToBangla(englishText: String): String {
        val words = englishText.split(" ")
        val banglaText = words.joinToString(" ") { word ->
            transliterationMap[word] ?: word.map { char ->
                transliterationMap[char.toString()] ?: char.toString()
            }.joinToString("")
        }
        return banglaText
    }

    val transliterationMap = mapOf(
        "a" to "আ",
        "b" to "ব",
        "c" to "স",
        "d" to "ড",
        "e" to "ই",
        "f" to "এফ",
        "g" to "জি",
        "h" to "এইচ",
        "i" to "আই",
        "j" to "জে",
        "k" to "কে",
        "l" to "এল",
        "m" to "এম",
        "n" to "এন",
        "o" to "ও",
        "p" to "পি",
        "q" to "কিউ",
        "r" to "আর",
        "s" to "এস",
        "t" to "টি",
        "u" to "উ",
        "v" to "ভি",
        "w" to "ডব্লিউ",
        "x" to "এক্স",
        "y" to "ওয়াই",
        "z" to "জেড",
        "How are you?" to "হাউ আর ইউ?",
        "Hello" to "হ্যালো"
    )
}
