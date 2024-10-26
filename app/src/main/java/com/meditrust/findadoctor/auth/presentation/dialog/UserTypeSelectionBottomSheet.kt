package com.meditrust.findadoctor.auth.presentation.dialog

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.auth.data.model.User
import com.meditrust.findadoctor.databinding.UserTypeSelectionSheetBinding

class UserTypeSelectionBottomSheet : BottomSheetDialogFragment() {

    private  val TAG = "UserTypeSelectionBottom"

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

        binding.cardDoctor.setOnClickListener { navigateToDoctorProfileInfo() }
        binding.cardPatient.setOnClickListener { onPatientCardSelected() }
    }

    private fun navigateToDoctorProfileInfo() {
        dismiss()
    }

    private fun onPatientCardSelected() {
        setPatientData()
//        setOnboardingCompleted()
//        findNavController().navigate(R.id.action_userTypeSelectionBottomSheet_to_homeFragment)
        dismiss()
    }

    private fun setPatientData() {
        val uid= FirebaseAuth.getInstance().currentUser?.uid
        val user = User(
            user_id = uid ?: "",
            name = "Patient",
            phone = "1", // or any default phone value
            profile_image = "", // or provide a default URL
            security_level = "1" // assign a default security level
        )

        FirebaseDatabase.getInstance().reference
            .child("users") // or your database node path
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User data inserted successfully.")
                } else {
                    Log.d(TAG, "Error inserting user data.", task.exception)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Something went wrong.", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()

                // Redirect the user to the login screen
              //  redirectLoginScreen()
            }

    }

    private fun setOnboardingCompleted() {
        requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit().apply {
            putBoolean("onboarding_completed", true)
            apply()
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