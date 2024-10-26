package com.meditrust.findadoctor.auth.presentation.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.ForgotPasswordSheetBinding

class ForgotPasswordBottomSheet : BottomSheetDialogFragment() {

    private  val TAG = "ForgotPasswordBottomShe"

    private var _binding: ForgotPasswordSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ForgotPasswordSheetBinding.inflate(inflater, container, false)
        binding.progressBar.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheetBehavior()
        configureButtonListeners(view)
    }

    private fun configureButtonListeners(view: View) {
           with(binding){
               btnResetPassword.setOnClickListener{
                   hideKeyboard(view)
                   setupForgotPassword()
               }
               btnBackLogin.setOnClickListener{
                   hideKeyboard(view)
                   dismiss()
               }
           }
    }

    private fun setupForgotPassword() {
         val email = binding.inputEmail.text.toString()
         if (email.isNotEmpty()) {
             binding.progressBar.show()
             sendPasswordResetEmail(email)
         } else {
             binding.tvForgotPasswordErrors.text = "Email field cannot be empty. Please enter your email."
         }
    }



    fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.hide()
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: Password Reset Email sent.")
                    Toast.makeText(context, "Password Reset Link Sent to Email", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Log.d(TAG, "onComplete: No user associated with that email.")
                    binding.tvForgotPasswordErrors.text="No User is Associated with that Email"
                }

            }
    }





    private fun setupBottomSheetBehavior() {
        val bottomSheet =
            dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.peekHeight = resources.displayMetrics.heightPixels
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Handle state changes if necessary
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle sliding behavior if necessary
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*   override fun onActivityCreated(savedInstanceState: Bundle?) {
           super.onActivityCreated(savedInstanceState)
           val touchOutsideView = dialog?.window
               ?.decorView
               ?.findViewById<View>(com.google.android.material.R.id.touch_outside)
           touchOutsideView?.setOnClickListener(null)
       }*/

}