package com.meditrust.findadoctor.profile.presentation.doctor

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.FilePaths.FIREBASE_DOCTOR_IMAGE_STORAGE
import com.meditrust.findadoctor.profile.dialog.CameraxBottomSheet
import com.meditrust.findadoctor.core.util.PersistenceUtil
import com.meditrust.findadoctor.core.util.UserGenders
import com.meditrust.findadoctor.core.util.UserRoles
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.databinding.FragmentDoctorProfileInfoBinding
import com.meditrust.findadoctor.profile.data.model.Doctor
import com.meditrust.findadoctor.profile.viewmodel.DoctorProfileViewModel
import com.meditrust.findadoctor.profile.viewmodel.SharedViewModel
import timber.log.Timber


class DoctorProfileInfoFragment : Fragment() {

    private var _binding: FragmentDoctorProfileInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var persistenceUtil: PersistenceUtil
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: DoctorProfileViewModel by activityViewModels()

    //camera permission
    private var mPermissionsChecked: Boolean = false
    private val VERIFY_PERMISSIONS_REQUEST = 1235


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        persistenceUtil = PersistenceUtil(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorProfileInfoBinding.inflate(inflater, container, false)
        toggleProgressBar(false)
        setupValidation()
        setupImageObserver()
        return binding.root
    }

    private fun setupValidation() {
        // Add a listener to check for changes in the radio buttons
        with(binding) {
            radioMale.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    radioFemale.isChecked = false
                }
            }

            radioFemale.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    radioMale.isChecked = false
                }
            }
        }
    }

    private fun setupImageObserver() {
        sharedViewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            Timber.d("Image URI: $uri")
            binding.imgProfile.setImageURI(uri)
            saveProfileImage(uri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

        viewModel.doctorData.observe(viewLifecycleOwner) { doctor ->
            doctor?.let {
                populateDoctorFields(it)
            } ?: Log.e("DoctorProfileFragment", "No doctor data available")
        }

        viewModel.loadDoctorData(uid)
    }

    private fun populateDoctorFields(doctor: Doctor) {
        with(binding) {
            inputTitle.setText(doctor.title)
            inputName.setText(doctor.name)
            inputEmail.setText(doctor.email)
            inputPhoneNumber.setText(doctor.phone)
            inputSpeciality.setText(doctor.specialization)
            inputDoctorType.setText(doctor.doctor_type)
            inputMedicalLicense.setText(doctor.medical_license_number)
            inputExperience.setText(doctor.experience)
            inputClinicName.setText(doctor.clinic_name)
            inputClinicAddress.setText(doctor.clinic_address)
            inputPhoneNumber.setText(doctor.clinic_contact)
            inputAvailability.setText(doctor.availability)
            inputConsultationFee.setText(doctor.appointment_fee)
            inputDob.setText(doctor.dob)
            radioMale.isChecked = doctor.gender == UserGenders.MALE
            radioFemale.isChecked = doctor.gender == UserGenders.FEMALE
            inputLanguage.setText(doctor.languages_spoken.joinToString(", "))
            inputAward.setText(doctor.certifications.joinToString(", "))
            inputDegree.setText(doctor.degrees)
            inputAboutMe.setText(doctor.about_me)
            loadProfileImage(doctor.profile_image)
        }
    }

    private fun loadProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(if (imageUrl.isNotEmpty()) imageUrl else R.drawable.ic_profile_avatar)
            .into(binding.imgProfile)
    }


    private fun collectDoctorDataWithValidation(): Doctor? {
        // Collect data from EditText fields
        val name = binding.inputName.text.toString().trim()
        val email = binding.inputEmail.text.toString().trim()
        val phoneNumber = binding.inputPhoneNumber.text.toString().trim()

        // Validate name field (non-empty)
        if (name.isEmpty()) {
            binding.layoutName.error = "Name is required"
            return null
        }

        // Validate email field (basic email format check)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.error = "Invalid email format"
            return null
        }

        // Validate phone number (optional validation, e.g., minimum length)
        if (phoneNumber.length < 10) {
            binding.layoutPhoneNumber.error = "Invalid phone number"
            return null
        }

        // If validation passes, return the collected form data
        return Doctor(
            name = name,
            email = email,
            phone = phoneNumber
        )
    }


    private fun setupListener() {
        skipListener()
        saveListener()
        profileImageListener()
    }

    private fun profileImageListener() {
        binding.btnAddImage.setOnClickListener {
            mPermissionsChecked = checkPermissions()
            if (mPermissionsChecked) {
                setPicture()
            } else {
                checkPermissions()
            }
        }
    }

    private fun setPicture() {
        val photoOptions = arrayOf(
            getString(R.string.take_photo),
            getString(R.string.choose_gallery)
        )

        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.choose_photo))

        // TODO: The setItems function is used to create a list of content
        builder.setItems(photoOptions) { _, which ->
            val photoOption = photoOptions[which]
            when (photoOption) {
                getString(R.string.take_photo) -> {
                    // Show Camerax BottomSheet
                    val cameraxDialog = CameraxBottomSheet()
                    cameraxDialog.show(
                        requireActivity().supportFragmentManager,
                        "CameraSheetDialog"
                    )
                }

                getString(R.string.choose_gallery) -> {
                    // Open the gallery
                    openGallery()
                }
            }
        }

        builder.setNegativeButton(R.string.mp_cancel, null)
        builder.show()
    }

    // Registers a photo picker activity launcher in single-select mode
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            // Callback is invoked after the user selects a media item or closes the photo picker
            if (uri != null) {
                sharedViewModel.setImageUri(uri)
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    private fun openGallery() {
        // Launch the photo picker and let the user choose only images
        pickMedia.launch(
            PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()
        )
    }


    private fun saveListener() {
        binding.btnSave.setOnClickListener {
            persistenceUtil.setProfileCompleted(true)
            hideKeyboard(it)
            toggleProgressBar(true)
            collectAndUpdateDoctorData()

        }
    }

    private fun updateDoctorProfile() {
        if (binding.inputEmail.text.toString() != FirebaseAuth.getInstance().currentUser?.email) {
            // make sure email and current password fields are filled

        } else {
            Toast.makeText(
                requireContext(),
                "Email and Current Password Fields Must be Filled to Save",
                Toast.LENGTH_SHORT
            ).show()
        }
        val reference = FirebaseDatabase.getInstance().reference
        /*
        ------ Change Name -----
        */
        if (binding.inputName.text.toString() != "") {
            reference.child("doctors")
                .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                .child("name")
                .setValue(binding.inputName.text.toString())
        }


    }


    private fun saveProfileImage(uri: Uri) {
        toggleProgressBar(true)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val imageRef = FirebaseStorage.getInstance()
            .getReference("$FIREBASE_DOCTOR_IMAGE_STORAGE/$uid/profile_image")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                    toggleProgressBar(false)
                    if (task.isSuccessful) {
                        task.result?.let { downloadUri ->
                            saveImageUrlToDatabase(downloadUri.toString(), uid)
                            showToast("Image Uploaded Successfully")
                        }
                    } else {
                        task.exception?.let { exception ->
                            Log.e("TAG", "Failed to retrieve download URI", exception)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                toggleProgressBar(false)
                Log.e("TAG", "Image upload failed", exception)
            }
    }

    private fun saveImageUrlToDatabase(url: String, uid: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        databaseRef.child("doctors")
            .child(uid)
            .child("profile_image")
            .setValue(url)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun skipListener() {
        binding.toolbar.setNavigationOnClickListener {
            persistenceUtil.setProfileCompleted(true)
            navigateToHome()
            hideKeyboard(it)
        }
    }

    private fun checkPermissions(): Boolean {
        val cameraGranted = ContextCompat.checkSelfPermission(
            requireActivity(), android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED


        val perms = mutableListOf<String>()
        if (!cameraGranted) {
            perms.add(android.Manifest.permission.CAMERA)
        }

        // Check for storage access on Android 14
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Option 1: Request specific media access (recommended)
            if (!hasPermission(android.Manifest.permission.READ_MEDIA_VIDEO) && !hasPermission(
                    android.Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                perms.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                perms.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Option 2: Request broader storage access (use with caution)
            if (ContextCompat.checkSelfPermission(
                    requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                perms.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        return if (perms.isNotEmpty()) {
            requestPermissions(perms.toTypedArray(), VERIFY_PERMISSIONS_REQUEST)
            false
        } else {
            true
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == VERIFY_PERMISSIONS_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions are granted
                mPermissionsChecked = true
                setPicture()
            } else {
                // Permissions are denied
                mPermissionsChecked = false
            }
        }
    }


    private fun collectAndUpdateDoctorData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val reference = FirebaseDatabase.getInstance().reference.child("doctors").child(uid)
        val databaseRef = FirebaseDatabase.getInstance().reference

        // Collect input data and update each field separately in Firebase if it's not empty
        if (binding.inputTitle.text.toString().isNotEmpty()) {
            reference.child("title").setValue(binding.inputTitle.text.toString().trim())
        }
        if (binding.inputName.text.toString().isNotEmpty()) {
            reference.child("name").setValue(binding.inputName.text.toString().trim())
        }
        if (binding.inputEmail.text.toString().isNotEmpty()) {
            reference.child("email").setValue(binding.inputEmail.text.toString().trim())
        }
        if (binding.inputPhoneNumber.text.toString().isNotEmpty()) {
            reference.child("phone").setValue(binding.inputPhoneNumber.text.toString().trim())
        }
        if (binding.inputDob.text.toString().isNotEmpty()) {
            reference.child("dob").setValue(binding.inputDob.text.toString().trim())
        }
        if (binding.inputSpeciality.text.toString().isNotEmpty()) {
            reference.child("specialization")
                .setValue(binding.inputSpeciality.text.toString().trim())
            databaseRef.child("specializations")
                .child(binding.inputSpeciality.text.toString().trim()).setValue(true)
        }
        if (binding.inputDoctorType.text.toString().isNotEmpty()) {
            reference.child("doctor_type").setValue(binding.inputDoctorType.text.toString().trim())
        }
        if (binding.inputMedicalLicense.text.toString().isNotEmpty()) {
            reference.child("medical_license_number")
                .setValue(binding.inputMedicalLicense.text.toString().trim())
        }
        if (binding.inputExperience.text.toString().isNotEmpty()) {
            reference.child("experience").setValue(binding.inputExperience.text.toString().trim())
        }
        if (binding.inputClinicAddress.text.toString().isNotEmpty()) {
            reference.child("clinic_address")
                .setValue(binding.inputClinicAddress.text.toString().trim())
        }
        if (binding.inputClinicName.text.toString().isNotEmpty()) {
            reference.child("clinic_name")
                .setValue(binding.inputClinicName.text.toString().trim())
        }
        if (binding.inputPhoneNumber.text.toString()
                .isNotEmpty()
        ) { // clinic_contact uses same input
            reference.child("clinic_contact")
                .setValue(binding.inputPhoneNumber.text.toString().trim())
        }
        if (binding.inputAvailability.text.toString().isNotEmpty()) {
            reference.child("availability")
                .setValue(binding.inputAvailability.text.toString().trim())
        }
        if (binding.inputConsultationFee.text.toString().isNotEmpty()) {
            reference.child("appointment_fee")
                .setValue(binding.inputConsultationFee.text.toString().trim())
        }

        val selectedValue =
            if (binding.radioMale.isChecked) UserGenders.MALE else UserGenders.FEMALE

        if (selectedValue.isNotEmpty()) {
            reference.child("gender").setValue(selectedValue)
        }

        // Split languages and certifications into lists
        val languagesSpoken = binding.inputLanguage.text.toString().split(",").map { it.trim() }
            .filter { it.isNotEmpty() }
        if (languagesSpoken.isNotEmpty()) {
            reference.child("languages_spoken").setValue(languagesSpoken)
        }

        val certifications = binding.inputAward.text.toString().split(",").map { it.trim() }
            .filter { it.isNotEmpty() }
        if (certifications.isNotEmpty()) {
            reference.child("certifications").setValue(certifications)
        }

        if (binding.inputDegree.text.toString().isNotEmpty()) {
            reference.child("degrees").setValue(binding.inputDegree.text.toString().trim())
        }

        if (binding.inputAboutMe.text.toString().isNotEmpty()) {
            reference.child("about_me").setValue(binding.inputAboutMe.text.toString().trim())
        }

        // Verified field is always set to "false" unless specified
        reference.child("verified").setValue("false")
        toggleProgressBar(false)
        navigateToHome()
    }

    private fun navigateToHome() {

        findNavController().navigate(R.id.action_doctorProfileInfoFragment_to_homeFragment)
    }

    private fun toggleProgressBar(shouldShow: Boolean) {
        if (shouldShow) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }
}

