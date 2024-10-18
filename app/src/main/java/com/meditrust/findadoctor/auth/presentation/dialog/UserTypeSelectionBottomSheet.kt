package com.meditrust.findadoctor.auth.presentation.dialog

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.UserTypeSelectionSheetBinding

class UserTypeSelectionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: UserTypeSelectionSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserTypeSelectionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*  binding.imgClose.setOnClickListener {
              findNavController().navigate(R.id.action_userTypeSelectionBottomSheet_to_loginFragment)
              dismiss()
          }*/
        binding.cardDoctor.setOnClickListener {
            findNavController().navigate(R.id.action_userTypeSelectionBottomSheet_to_doctorProfileInfoFragment)
            dismiss()
        }
        binding.cardPatient.setOnClickListener{
            val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("onboarding_completed", true)
                apply()
            }
            findNavController().navigate(R.id.action_userTypeSelectionBottomSheet_to_homeFragment)
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val touchOutsideView = dialog?.window
            ?.decorView
            ?.findViewById<View>(com.google.android.material.R.id.touch_outside)
        touchOutsideView?.setOnClickListener(null)
    }

}