package com.meditrust.findadoctor.account.presentation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.UserGenders
import com.meditrust.findadoctor.core.util.UserRoles
import com.meditrust.findadoctor.databinding.FragmentAccountSettingsBinding
import com.meditrust.findadoctor.profile.data.model.Admin
import com.meditrust.findadoctor.profile.data.model.Doctor
import com.meditrust.findadoctor.profile.data.model.Patient
import com.meditrust.findadoctor.profile.viewmodel.DoctorProfileViewModel
import kotlin.getValue


class AccountSettingsFragment : Fragment() {

    private val TAG = "AccountSettingsFragment"
    private var _binding: FragmentAccountSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DoctorProfileViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupObject()
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
        _binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarNavigation()
        checkFirebaseAuthState()
        setupListener()

    }

    private fun setupListener() {
        appControlListener();
    }

    private fun appControlListener() {
        with(binding){
            layoutSignOutContainer.setOnClickListener {
                signOut()
                findNavController().navigate(R.id.action_accountSettingsFragment_to_loginFragment)
            }
            layoutRateAppContainer.setOnClickListener{
                rateSoluta()
            }
            layoutHelpContainer.setOnClickListener{
                findNavController().navigate(R.id.action_accountSettingsFragment_to_messageFragment)
            }
            layoutChangePasswordContainer.setOnClickListener{
                findNavController().navigate(R.id.action_accountSettingsFragment_to_forgotPasswordBottomSheet)
            }
        }
    }
    private fun rateSoluta() {
        val appPackageName = "com.soluta.business"
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }


    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }

    private fun observeDoctorData() {
        viewModel.doctorData.observe(viewLifecycleOwner) { doctor ->
            if (doctor != null) {
                populateDoctorFields(doctor)
            } else {
                Log.e("DoctorProfileFragment", "No doctor data available")
            }
        }
    }


    private fun populateDoctorFields(doctor: Doctor) {

        with(binding) {
            tvProfileName.text = doctor.title + " " + doctor.name
            tvProfileTypeName.text = doctor.user_role
            loadProfileImage(doctor.profile_image)
        }

    }

    private fun loadProfileImage(imageUrl: String) {

        Glide.with(this)
            .load(if (imageUrl.isNotEmpty()) imageUrl else R.drawable.ic_profile_avatar)
            .into(binding.profileImage)
    }

    private fun setupToolbarNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkFirebaseAuthState() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null)
            if (currentUser.isAnonymous) {
                Log.d(TAG, "checkFirebaseAuthState: guest ${currentUser.uid}")
                accountSetupForGuest(currentUser)
            } else {
                Log.d(TAG, "checkFirebaseAuthState: authrize${currentUser.uid}")
                accountSetupForDoctorAndAdmin(currentUser)
            }
    }

    private fun accountSetupForGuest(user: FirebaseUser) {

    }

    private fun accountSetupForDoctorAndAdmin(user: FirebaseUser) {

        val userRole = user.displayName.takeIf { !it.isNullOrBlank() } ?: "guest"
        setupAccountSettingsBasedOnUserRole(userRole)

    }

    private fun setupAccountSettingsBasedOnUserRole(userRole: String) {
        if (isAdded) {  // Check if fragment is added before navigation
            when (userRole) {
                UserRoles.ADMIN -> {
                    accountSetupForAdmin()
                }

                UserRoles.DOCTOR -> {
                    accountSetupForDoctor()
                }

                UserRoles.PATIENT -> {
                    accountSetupForPatient()
                }

                else -> Log.d(TAG, "Unknown user role: $userRole")
            }
        } else {
            Log.e(TAG, "Fragment is not added. Skipping navigation.")
        }
    }

    private fun accountSetupForPatient() {
        with(binding){
            tvStoreSetup.visibility = View.GONE
            layoutCategorySetup.visibility = View.GONE
            layoutProductSetup.visibility = View.GONE
        }
      val uid = getCurrentUserId()
        fetchPatientData(uid)

    }

    private fun accountSetupForAdmin() {
        with(binding){
            tvStoreSetup.visibility = View.VISIBLE
            layoutCategorySetup.visibility = View.VISIBLE
            layoutProductSetup.visibility = View.VISIBLE
        }
        val uid = getCurrentUserId()
        fetchAdminData(uid)
    }

    private fun fetchAdminData(adminId: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val adminRef = databaseRef.child("admins").child(adminId)
    
        adminRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val adminData = snapshot?.getValue(Admin::class.java)
                if (adminData != null) {
                    setupAdminData(adminData)
                } else {
                    Log.d(TAG, "fetchAdminData: Admin data is null")
                }
            } else {
                Log.d(TAG, "Error fetching admin data", task.exception)
            }
        }
    }

    private fun setupAdminData(admin: Admin) {
        with(binding){
            tvProfileName.text = admin.name
            tvProfileTypeName.text = admin.user_role
            loadProfileImage(admin.profile_image)
        }
    }


    private fun accountSetupForDoctor() {
        with(binding){
            tvStoreSetup.visibility = View.GONE
            layoutCategorySetup.visibility = View.GONE
            layoutProductSetup.visibility = View.GONE
        }
        val uid = getCurrentUserId()
        observeDoctorData()
        viewModel.loadDoctorData(uid)
    }
    private fun setupObject() {
        auth = FirebaseAuth.getInstance()

    }
    private fun signOut() {
        auth.signOut()
    }

    private fun fetchPatientData(patientId: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val patientRef = databaseRef.child("patients").child(patientId)

        patientRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val patient = snapshot?.getValue(Patient::class.java)
                if (patient != null) {
                    setupPatientData(patient)
                } else {
                    Log.d(TAG, "fetchPatientData: Patient data is null or improperly structured")
                }
            } else {
                Log.d(TAG, "Error fetching patient data", task.exception)
            }
        }
    }


    private fun mapToPatient(patientMap: HashMap<String, Any>): Patient {
        val name = patientMap["name"] as? String ?: "Unknown"
        val userRole = patientMap["user_role"] as? String ?: "Unknown"
        val profileImage = patientMap["profile_image"] as? String ?: ""

        return Patient(name, userRole, profileImage)
    }


    private fun setupPatientData(patient: Patient) {
        with(binding){
            tvProfileName.text = patient.name
            tvProfileTypeName.text = patient.user_role
            loadProfileImage(patient.profile_image)
        }
    }


}

