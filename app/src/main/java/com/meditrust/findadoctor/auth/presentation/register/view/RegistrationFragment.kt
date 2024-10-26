package com.meditrust.findadoctor.auth.presentation.register.view

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.auth.data.repository.SpecializationProvider
import com.meditrust.findadoctor.core.util.NoUnderlineClickableSpan
import com.meditrust.findadoctor.core.util.TextViewUtils
import com.meditrust.findadoctor.core.util.UserRoles
import com.meditrust.findadoctor.core.util.hideKeyboard
import com.meditrust.findadoctor.core.util.isValidEmail
import com.meditrust.findadoctor.databinding.FragmentRegistrationBinding
import com.meditrust.findadoctor.profile.data.model.Admin
import com.meditrust.findadoctor.profile.data.model.Appointment
import com.meditrust.findadoctor.profile.data.model.Appointments
import com.meditrust.findadoctor.profile.data.model.Doctor
import com.meditrust.findadoctor.profile.data.model.Patient
import com.meditrust.findadoctor.profile.data.model.Review
import com.meditrust.findadoctor.profile.domain.UserProfileManager


class RegistrationFragment : Fragment() {

    private val TAG = "RegistrationFragment"
    private val DOMAIN_NAME = "gmail.com"

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    // Instance of SpecializationProvider
    private lateinit var specializationProvider: SpecializationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        specializationProvider = SpecializationProvider()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        setupUIComponents()
        setupValidation()
        return binding.root
    }


    private fun setupUIComponents() {
        setupSignInText()
        setupPrivacyPolicy()
        binding.progressBar.hide()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupCreateAccountButton(view)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupCreateAccountButton(view: View) {
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            // val confirmPassword = binding.inputConfirmPassword.text.toString()
            registerNewEmail(email, password)
            hideKeyboard(view)
        }


    }

    private fun registerNewEmail(email: String, password: String) {
        binding.progressBar.show()
       //Insert User Data
        val userRole = if (binding.radioPatient.isChecked) UserRoles.PATIENT else UserRoles.DOCTOR
//        val userRole = UserRoles.ADMIN
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: AuthState: ${FirebaseAuth.getInstance().currentUser?.uid}")
                    sendVerificationEmail()
                    insertUserData(userRole)
                    updateUserProfile(userRole)
                    clearAllFields()
                } else {
                    Toast.makeText(context, "Unable to Register", Toast.LENGTH_SHORT).show()
                }
                binding.progressBar.hide()
            }
    }

    private fun updateUserProfile(name: String) {
        // Usage example (assuming you have an instance of FirebaseAuth):
        val firebaseAuth = FirebaseAuth.getInstance()
        val userProfileManager = UserProfileManager(firebaseAuth)

        userProfileManager.updateUserProfile(name, Uri.parse(""))
    }

    private fun insertUserData(userRole: String) {
        when (userRole) {
            UserRoles.DOCTOR -> insertDoctorData(userRole)
            UserRoles.PATIENT -> insertPatientData(userRole)
            UserRoles.GUEST -> insertGuestData()
            UserRoles.ADMIN -> insertAdminData(userRole)
            else -> Log.d(TAG, "Unknown user role: $userRole")
        }
    }

    private fun insertGuestData() {
        Log.d(TAG, "Inserting guest-specific data.")
    }

    private fun insertAdminData(userRole: String) {
        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        /// Sample appointment data

      // Sample admin data
        val admin = Admin(
            admin_id = uid,
            user_role = userRole,
            name = name,
            email = email,
            phone_number = "01717121839",
            profile_image = "https://firebasestorage.googleapis.com/v0/b/projectblueprints-1d8f3.appspot.com/o/images%2Fusers%2FjwMV3SaYMgNmJXZBSMJs8dDKIvv2%2Fimage%20(1).png?alt=media&token=5c20c33d-1b84-4677-a3a2-fc3fd9d20833", // Optional: URL or empty string
            address = "Kharia para,Phulpur,Mymensingh",
        )

// Insert patient data into Firebase Realtime Database
        val databaseRef = FirebaseDatabase.getInstance().reference
        databaseRef.child("admins").child(admin.admin_id).setValue(admin)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    redirectLoginScreen()
                    Log.d("Firebase", "Admin data inserted successfully.")
                } else {
                    FirebaseAuth.getInstance().signOut()
                    redirectLoginScreen()
                    Log.e("Firebase", "Failed to insert admin data.", task.exception)
                }
            }

        Log.d(TAG, "Inserting admin-specific data.")
    }

    private fun insertDoctorData(userRole: String) {
        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        // Generate random specialization using the SpecializationProvider class
        val randomSpecialization = specializationProvider.getRandomSpecialization()
        val doctor = Doctor(
            doctor_id = uid,
            user_role = userRole,
            title = "Prof. Dr",
            name = name,
            email = email,
            phone = "+1234567890",
            dob="20 july 1982",
            specialization = randomSpecialization,
            profile_image = "",
            doctor_type = "Medical",
            medical_license_number = "18273",
            experience = "10", // Years of experience as a String
            gender = "",
            clinic_name = "Health Clinic",
            clinic_address = "123 Health St.",
            clinic_contact = "+1234567891",
            availability = "Mon-Fri, 9 AM - 5 PM",
            total_rating = "9", // Total rating as a String
            ratings_count = "2", // Ratings count as a String
            average_rating = "4.5", // Average rating as a String
            review_score = "4.7", // Review score as a String
            appointment_fee = "$100", // Appointment fee as a String
            in_person = "true", // In-person consultation as a String
            online = "true", // Online consultation as a String
            languages_spoken = listOf("English", "Spanish"), // List of spoken languages
            certifications = listOf("Board Certified in Cardiology"), // List of certifications
            degrees = "MBBS, FCPS",
            about_me = "Tell about yourself",
            verified = "false", // Verified status as a String
            medical_school = "Harvard Medical School", // Medical school as a String
            graduation_year = "2008", // Graduation year as a String
            reviews = mapOf(
                "reviewId1" to Review("5", "Excellent doctor!", "patientId1", "2024-09-16"),
                "reviewId2" to Review("4", "Very good experience.", "patientId2", "2024-09-15")
            ),
            appointments = mapOf(
                "appointmentId1" to Appointment("patientId1", "2024-09-20", "10:00 AM", "Confirmed")
            )
        )

// Insert the doctor data into Firebase
        val databaseRef = FirebaseDatabase.getInstance().reference

        databaseRef.child("doctors").child(uid).setValue(doctor)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    redirectLoginScreen()
                    Log.d("Firebase", "Doctor data inserted successfully.")
                } else {
                    FirebaseAuth.getInstance().signOut()
                    redirectLoginScreen()
                    Log.e("Firebase", "Failed to insert doctor data.", task.exception)
                    Toast.makeText(requireContext(), "something went wrong.", Toast.LENGTH_SHORT)
                        .show();
                }
            }
        // Add the specialization to 'specializations' node (if not already present)
        databaseRef.child("specializations").child(randomSpecialization).setValue(true)

    }

    private fun insertPatientData(userRole: String) {
        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        /// Sample appointment data
      /*  val appointment1 = Appointments(
            appointment_id = "appointmentId1",
            doctor_id = "doctorId1",
            date = "2024-10-20",
            time = "10:00 AM",
            status = "Upcoming",
            rating = "5"
        )*/

// Sample patient data
        val patient = Patient(
            patient_id = uid,
            user_role = userRole,
            name = name,
            email = email,
            phone_number = "+1234567890",
            profile_image = "", // Optional: URL or empty string
            address = "123 Main St, City, Country",
            medical_history = "No significant medical history", // Optional: Medical history
            saved_doctors = mutableListOf(), // List of saved doctor IDs
            appointments = mutableListOf() // List of patient appointments
        )


// Insert patient data into Firebase Realtime Database
        val databaseRef = FirebaseDatabase.getInstance().reference
        databaseRef.child("patients").child(patient.patient_id).setValue(patient)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    redirectLoginScreen()
                    Log.d("Firebase", "Patient data inserted successfully.")
                } else {
                    FirebaseAuth.getInstance().signOut()
                    redirectLoginScreen()
                    Log.e("Firebase", "Failed to insert patient data.", task.exception)
                }
            }
        // Add the specialization to 'specializations' node (if not already present)
        databaseRef.child("specializations").child("Cardiology").setValue(true);
        Log.d(TAG, "Inserting patient-specific data.")
    }

    private fun redirectLoginScreen() {
        findNavController().navigate(R.id.action_registrationFragment_to_emailVerificationGuidelineBottomSheet)
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Sent Verification Email", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, "Couldn't Send Verification Email", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupValidation() {
        with(binding) {
            val textWatcherName = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val fullName = s.toString()
                    if (fullName.isEmpty()) {
                        layoutName.error = "Please enter your full name"
                    } else {
                        layoutName.error = null
                    }
                    updateButtonState()
                }
            }
            val textWatcherEmail = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val email = s.toString()
//                    if (!email.isValidDomain(DOMAIN_NAME)) {
                    if (!email.isValidEmail()) {
                        layoutEmail.error = "please enter a valid email"
                    } else {
                        layoutEmail.error = null
                    }
                    updateButtonState()
                }
            }
            val textWatcherPassword = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val password = s.toString()
                    if (password.length < 8) {
                        layoutPassword.error = "Password must be at least 8 characters"
                    } else {
                        layoutPassword.error = null
                    }
                    updateButtonState()
                }
            }

            // Store references to the TextWatchers
            inputName.addTextChangedListener(textWatcherName)
            inputEmail.addTextChangedListener(textWatcherEmail)
            inputPassword.addTextChangedListener(textWatcherPassword)


            // Add a listener to check for changes in the radio buttons
            radioPatient.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    radioDoctor.isChecked = false
                }
                updateButtonState()
            }

            radioDoctor.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    radioPatient.isChecked = false
                }
                updateButtonState()
            }

            // Validate before submitting the form


        }
    }

    fun validate() {
        if (!binding.radioPatient.isChecked && !binding.radioDoctor.isChecked) {
            // Show an error message or prevent form submission
            Toast.makeText(requireContext(), "Please select your role", Toast.LENGTH_LONG).show()

        } else {
            val selectedValue =
                if (binding.radioPatient.isChecked) UserRoles.PATIENT else UserRoles.DOCTOR
        }
    }

    private fun updateButtonState() {
        binding.btnCreateAccount.isEnabled = isFormValid()
    }

    private fun isFormValid(): Boolean {
        val isNameValid =
            binding.layoutName.error == null && binding.inputName.text?.isNotEmpty() == true
        val isEmailValid =
            binding.layoutEmail.error == null && binding.inputEmail.text?.isNotEmpty() == true
        val isPasswordValid =
            binding.layoutPassword.error == null && binding.inputPassword.text?.isNotEmpty() == true
        val isUserRoleValid = binding.radioPatient.isChecked || binding.radioDoctor.isChecked
        return isNameValid && isEmailValid && isPasswordValid && isUserRoleValid
    }


    private fun clearAllFields() {
        with(binding) {
            inputName.text?.clear()
            layoutName.error = null
            inputName.clearFocus()

            inputEmail.text?.clear()
            layoutEmail.error = null
            inputEmail.clearFocus()

            inputPassword.text?.clear()
            layoutPassword.error = null
            inputPassword.clearFocus()

            radioDoctor.isChecked = false
            radioPatient.isChecked = false
        }
    }


    private fun setupPrivacyPolicy() {
        val policyText = "By sign up, you agree to our Terms of Use and Privacy Policy."
        val clickableParts = mapOf(
            "Terms of Use" to "https://healthengine.com.au/terms_text",
            "Privacy Policy" to "https://healthengine.com.au/privacy_text"
        )

        TextViewUtils.setupClickableTextView(
            requireContext(),
            binding.tvTerms,
            policyText,
            clickableParts
        )
    }

    private fun setupSignInText() {
        val text = getString(R.string.sign_in_prompt)
        val signInText = getString(R.string.sign_in_short)
        val spannableString = SpannableString(text)

        val start = text.indexOf(signInText)
        val end = start + signInText.length
        val clickableSpan = object : NoUnderlineClickableSpan() {
            override fun onClick(widget: View) {
                // Define your click action here
                findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
                hideKeyboard(widget)
            }
        }
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvSignIn.text = spannableString
        binding.tvSignIn.movementMethod = LinkMovementMethod.getInstance()  // Enable click handling
    }


}