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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.ForgotPasswordSheetBinding
import com.meditrust.findadoctor.databinding.ResendVerificationSheetBinding

class ResendVerificationBottomSheet : BottomSheetDialogFragment() {

    private  val TAG = "ForgotPasswordBottomShe"

    private var _binding: ResendVerificationSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResendVerificationSheetBinding.inflate(inflater, container, false)
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
               btnResendLink.setOnClickListener{
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
        val password = binding.inputPassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            binding.progressBar.show()
            authenticateAndResendEmail(email, password)
        } else {
            binding.tvVerifyEmailErrors.text = "Please enter both email and password."
        }
    }



    /**
     * Reauthenticate so we can send a verification email again
     * @param email
     * @param password
     */
    private fun authenticateAndResendEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: Reauthenticate success.")
                    sendVerificationEmail()
                    FirebaseAuth.getInstance().signOut()
                    binding.progressBar.hide()
                    dismiss()
                }
            }.addOnFailureListener { e ->
                binding.tvVerifyEmailErrors.text="Invalid Credentials. \nReset your password and try again"
                binding.progressBar.hide()
            }
    }


    /**
     * Sends an email verification link to the user
     */
    fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (isAdded) { // Check if the fragment is attached to the context
                    Toast.makeText(requireContext(), "Sent Verification Email", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.tvVerifyEmailErrors.text="Couldn't send email"
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