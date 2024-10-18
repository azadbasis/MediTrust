package com.meditrust.findadoctor.auth.presentation.login.view

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.NoUnderlineClickableSpan
import com.meditrust.findadoctor.core.util.TextViewUtils
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding
            ?: throw IllegalStateException("View binding is only valid between onCreateView and onDestroyView")

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupFirebaseAuth()
        setupUIComponents()


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
        setupButtonListeners(view)
        underlineText()
    }

    private fun setupFirebaseAuth() {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Check if the email is verified
                // TODO: Here we pass verified user and admin created user profile as well
                //  we provide special email containing "medi".  by checking this we bypass email verification
                //  or we can use specific domain like abc@medi.trust for random doctor profile

                if (user.isEmailVerified) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:${user.uid}")
                    Toast.makeText(context, "Authenticated with: ${user.email}", Toast.LENGTH_SHORT)
                        .show()
                    // TODO: Navigation graph use for home fragment 
                    /*   val intent = Intent(activity, SignedInActivity::class.java)
                       intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                       startActivity(intent)
                       activity?.finish()*/
                } else {
                    Toast.makeText(
                        context,
                        "Email is not Verified\nCheck your Inbox",
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                }
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
        }
    }


    private fun setupToolbarNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupButtonListeners(view: View) {
        binding.btnContinue.setOnClickListener {
            // findNavController().navigate(R.id.action_loginFragment_to_userTypeSelectionBottomSheet)
            binding.btnContinue.isEnabled=false
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
                            binding.tvSignInErrors.text="signInWithEmail: success"
                            clearAllFields()
                            binding.btnContinue.isEnabled=true
                        } else {
                            binding.tvSignInErrors.text="signInWithEmail: failure ${task.exception}"
                            binding.btnContinue.isEnabled=true
                        }
                    }.addOnFailureListener { e ->
                        binding.tvSignInErrors.text= "Authentication Failed: ${e.message}"
                        binding.btnContinue.isEnabled=true
                    }
               binding.btnContinue.isEnabled=true
            } else {
                binding.tvSignInErrors.text= "You didn't fill in all the fields."
                binding.btnContinue.isEnabled=true
            }
        }
    }


    private fun clearAllFields() {
        with(binding) {
            inputEmail.text?.clear()
            inputEmail.clearFocus()

            inputPassword.text?.clear()
            inputPassword.clearFocus()
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

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (::mAuthListener.isInitialized) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
