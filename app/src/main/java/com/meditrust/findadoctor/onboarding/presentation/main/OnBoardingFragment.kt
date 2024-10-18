package com.meditrust.findadoctor.onboarding.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.FragmentOnBoardingBinding
import com.meditrust.findadoctor.onboarding.presentation.loginoptions.LoginOptionsFragment
import com.meditrust.findadoctor.onboarding.presentation.welcome.WelcomeFragment


class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardingBinding
    private val fragmentList = arrayListOf<Fragment>(
        WelcomeFragment(),
        LoginOptionsFragment()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        setupViewPager()
        handleBackPressed()
        return binding.root
    }

    private fun setupViewPager() {
        val adapter = OnBoardingAdapter(
            fragmentList,
            childFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = adapter
        // Uncomment the line below if using a DotsIndicator with ViewPager2
        // binding.dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun handleBackPressed() {
        // Get the back press dispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Check if we're on the first page of the ViewPager2
                if (binding.viewPager.currentItem == 0) {
                    // If we are on the first page, allow normal back navigation (exit fragment)
                    isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    // Otherwise, move to the previous page in ViewPager2
                    binding.viewPager.currentItem = binding.viewPager.currentItem - 1
                }
            }
        })
    }
}
