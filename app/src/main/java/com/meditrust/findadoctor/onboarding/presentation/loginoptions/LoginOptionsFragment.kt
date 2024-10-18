package com.meditrust.findadoctor.onboarding.presentation.loginoptions

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.TextViewUtils
import com.meditrust.findadoctor.databinding.FragmentLoginOptionsBinding


class LoginOptionsFragment : Fragment() {

    private var _binding: FragmentLoginOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Set up click listeners and other UI interactions here
        setupClickListeners()
        setupPrivacyPolicy()
    }

    private fun setupClickListeners() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        binding.apply {
            btnBackWelcome.setOnClickListener {
                viewPager?.currentItem = 0
            }
        }
        binding.btnAsGuest.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("onboarding_completed", true)
                apply()
            }

            findNavController().navigate(R.id.action_onBoardingFragment_to_homeFragment)
        }

        binding.btnEmailLogin.setOnClickListener{

            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupPrivacyPolicy() {
        val policyText = "By continuing, you agree to our Terms of Use and Privacy Policy."
        val clickableParts = mapOf(
            "Terms of Use" to "https://healthengine.com.au/terms_text",
            "Privacy Policy" to "https://healthengine.com.au/privacy_text"
        )

        TextViewUtils.setupClickableTextView(requireContext(), binding.tvTermsPolicy, policyText, clickableParts)
    }
}
