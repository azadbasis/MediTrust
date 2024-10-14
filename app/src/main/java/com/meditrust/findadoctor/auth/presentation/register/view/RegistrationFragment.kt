package com.meditrust.findadoctor.auth.presentation.register.view

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.NoUnderlineClickableSpan
import com.meditrust.findadoctor.core.util.TextViewUtils
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.FragmentRegistrationBinding


class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        setupSignInText()
        setupPrivacyPolicy()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up toolbar back button listener
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            btnCreateAccount.setOnClickListener {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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