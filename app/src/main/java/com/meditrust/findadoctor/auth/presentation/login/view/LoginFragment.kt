package com.meditrust.findadoctor.auth.presentation.login.view

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.NoUnderlineClickableSpan
import com.meditrust.findadoctor.core.util.TextViewUtils
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw IllegalStateException("View binding is only valid between onCreateView and onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupSignUpText()
        setupPrivacyPolicy()

        return binding.root
    }

    private fun setupSignUpText() {
        val text = getString(R.string.sign_up_prompt)
        val signUpText = getString(R.string.sign_up)
        val spannableString = SpannableString(text)
        val signUpColorSpan = ForegroundColorSpan(requireContext().getColor(R.color.colorMediTrust))

        val start = text.indexOf(signUpText)
        val end = start + signUpText.length
        //spannableString.setSpan(signUpColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

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

        TextViewUtils.setupClickableTextView(requireContext(), binding.tvTerms, policyText, clickableParts)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up toolbar back button listener
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

                hideKeyboard(view)
            }
            tvForgotPassword.apply {
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
            tvVerifyAccount.apply {
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
