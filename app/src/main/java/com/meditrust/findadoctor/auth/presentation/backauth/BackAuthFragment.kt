package com.meditrust.findadoctor.auth.presentation.backauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.meditrust.findadoctor.R

import com.meditrust.findadoctor.databinding.FragmentBackAuthBinding

class BackAuthFragment : Fragment() {

    private var _binding: FragmentBackAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val navigationBackHandler = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    requireActivity().finish()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, navigationBackHandler)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentBackAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {

        with(binding){
            btnBackWelcome.setOnClickListener {
                requireActivity().onBackPressed()
            }
            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_backAuthFragment_to_registrationFragment)
            }
            btnEmailLogin.setOnClickListener {
                findNavController().navigate(R.id.action_backAuthFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
