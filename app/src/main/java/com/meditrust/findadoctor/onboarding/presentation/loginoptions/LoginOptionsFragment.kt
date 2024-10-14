package com.meditrust.findadoctor.onboarding.presentation.loginoptions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meditrust.findadoctor.R
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}