package com.meditrust.findadoctor.profile.presentation.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.FragmentDoctorProfileInfoBinding


class DoctorProfileInfoFragment : Fragment() {

    private var _binding: FragmentDoctorProfileInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentDoctorProfileInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }

    private fun setupListener() {
        skipListener()
        saveListener()
    }

    private fun saveListener() {
        binding.btnSave.setOnClickListener {

            findNavController().navigate(R.id.action_doctorProfileInfoFragment_to_homeFragment)
            hideKeyboard(it)
        }
    }

    private fun skipListener() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_doctorProfileInfoFragment_to_homeFragment)
            hideKeyboard(it)
        }
    }

}

