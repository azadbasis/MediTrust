package com.meditrust.findadoctor.auth.presentation.register.view

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils.isEmpty
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.NoUnderlineClickableSpan
import com.meditrust.findadoctor.core.util.TextViewUtils
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.core.util.isValidDomain
import com.meditrust.findadoctor.databinding.FragmentRegistrationBinding


class RegistrationFragment : Fragment() {

    private val TAG = "RegistrationFragment"
    private val DOMAIN_NAME = "gmail.com"

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        setupUIComponents()
        setupValidation()
        return binding.root
    }



    private fun setupUIComponents() {
        setupSignInText()
        setupPrivacyPolicy()
        binding.progressBar.hide()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupCreateAccountButton(view)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupCreateAccountButton(view: View) {
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            // val confirmPassword = binding.inputConfirmPassword.text.toString()
            registerNewEmail(email, password)
            hideKeyboard(view)
        }


    }

    private fun registerNewEmail(email: String, password: String) {
        binding.progressBar.show()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "createUserWithEmail:onComplete: ${task.isSuccessful}")
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: AuthState: ${FirebaseAuth.getInstance().currentUser?.uid}")
                    clearAllFields()
                    sendVerificationEmail()
                    FirebaseAuth.getInstance().signOut()
                } else {
                    Toast.makeText(context, "Unable to Register", Toast.LENGTH_SHORT).show()
                }
                binding.progressBar.hide()
            }
    }

    private fun redirectLoginScreen() {
        findNavController().navigate(R.id.action_registrationFragment_to_emailVerificationGuidelineBottomSheet)
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
              //  Toast.makeText(context, "Sent Verification Email", Toast.LENGTH_SHORT).show()
                redirectLoginScreen()
            } else {
                Toast.makeText(context, "Couldn't Send Verification Email", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun setupValidation() {
        with(binding) {
            val textWatcherName = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val fullName = s.toString()
                    if (fullName.isEmpty()) {
                        layoutName.error = "Please enter your full name"
                    } else {
                        layoutName.error = null
                    }
                    updateButtonState()
                }
            }
            val textWatcherEmail = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val email = s.toString()
                    if (!email.isValidDomain(DOMAIN_NAME)) {
                        layoutEmail.error = "please enter a valid email"
                    } else {
                        layoutEmail.error = null
                    }
                    updateButtonState()
                }
            }
            val textWatcherPassword = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val password = s.toString()
                    if (password.length < 8) {
                        layoutPassword.error = "Password must be at least 8 characters"
                    } else {
                        layoutPassword.error = null
                    }
                    updateButtonState()
                }
            }

            // Store references to the TextWatchers
            inputName.addTextChangedListener(textWatcherName)
            inputEmail.addTextChangedListener(textWatcherEmail)
            inputPassword.addTextChangedListener(textWatcherPassword)
            
        }
    }
    private fun updateButtonState() {
        binding.btnCreateAccount.isEnabled = isFormValid()
    }
    private fun isFormValid(): Boolean {
        val isNameValid = binding.layoutName.error == null && binding.inputName.text?.isNotEmpty() == true
        val isEmailValid = binding.layoutEmail.error == null && binding.inputEmail.text?.isNotEmpty() == true
        val isPasswordValid = binding.layoutPassword.error == null && binding.inputPassword.text?.isNotEmpty() == true
        return isNameValid && isEmailValid && isPasswordValid
    }


    private fun clearAllFields() {
        with(binding) {
            inputName.text?.clear()
            layoutName.error = null
            inputName.clearFocus()

            inputEmail.text?.clear()
            layoutEmail.error = null
            inputEmail.clearFocus()

            inputPassword.text?.clear()
            layoutPassword.error = null
            inputPassword.clearFocus()
        }
    }

























    private fun setupPrivacyPolicy() {
        val policyText = "By sign up, you agree to our Terms of Use and Privacy Policy."
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

    private fun setupSignInText() {
        val text = getString(R.string.sign_in_prompt)
        val signInText = getString(R.string.sign_in_short)
        val spannableString = SpannableString(text)

        val start = text.indexOf(signInText)
        val end = start + signInText.length
        val clickableSpan = object : NoUnderlineClickableSpan() {
            override fun onClick(widget: View) {
                // Define your click action here
                findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
                hideKeyboard(widget)
            }
        }
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSignIn.text = spannableString
        binding.tvSignIn.movementMethod = LinkMovementMethod.getInstance()  // Enable click handling
    }


}