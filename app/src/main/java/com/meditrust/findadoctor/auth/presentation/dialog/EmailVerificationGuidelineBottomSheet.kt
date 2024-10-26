package com.meditrust.findadoctor.auth.presentation.dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.EmailVerificationGuidelineSheetBinding

class EmailVerificationGuidelineBottomSheet : BottomSheetDialogFragment() {

    private var _binding: EmailVerificationGuidelineSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmailVerificationGuidelineSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set any view data or actions here
        binding.buttonDismiss.setOnClickListener {
            findNavController().navigate(R.id.action_emailVerificationGuidelineBottomSheet_to_loginFragment)
            dismiss()
        }
        binding.imgClose.setOnClickListener {
            findNavController().navigate(R.id.action_emailVerificationGuidelineBottomSheet_to_loginFragment)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onStart() {
        super.onStart()
        // Disable dismiss on drag down
        val bottomSheet = (dialog as? BottomSheetDialog)?.behavior
        bottomSheet?.isDraggable = false  // Disable drag-down dismiss
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false  // Prevent dismiss on outside touch
    }
}
