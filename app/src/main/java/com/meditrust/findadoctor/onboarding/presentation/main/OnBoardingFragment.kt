package com.meditrust.findadoctor.onboarding.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.FragmentOnBoardingBinding
import com.meditrust.findadoctor.onboarding.presentation.loginoptions.LoginOptionsFragment
import com.meditrust.findadoctor.onboarding.presentation.welcome.WelcomeFragment


class OnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val bind = FragmentOnBoardingBinding.inflate(inflater, container, false)
        val fragmentList = arrayListOf<Fragment>(
            WelcomeFragment(),
           LoginOptionsFragment()
        )

        val adapter = OnBoardingAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        bind.apply {
            viewPager.adapter = adapter
         //   dotsIndicator.setViewPager2(viewPager)
        }
        return bind.root

    }

}