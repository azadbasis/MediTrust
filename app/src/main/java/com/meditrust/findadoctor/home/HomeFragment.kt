package com.meditrust.findadoctor.home

import Product
import TextItem
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.meditrust.findadoctor.databinding.FragmentHomeBinding
import com.meditrust.findadoctor.home.data.model.Specialization
import com.meditrust.findadoctor.profile.data.model.Doctor
import  com.meditrust.findadoctor.R
import com.meditrust.findadoctor.core.util.DateUtils
import com.meditrust.findadoctor.home.data.model.TopRatedDoctor
import com.meditrust.findadoctor.profile.data.model.Appointments
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.toMutableList


class HomeFragment : Fragment(), ChildAdapter.OnItemClickListener {

    private val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var parentAdapter: ParentAdapter? = null
    private var screenWidth = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObject()
        getDisplayMetrics()
        setHasOptionsMenu(true)

        // New method to load all top, recent, and other doctors and specializations

    }

    private fun toggleProgressBar(shouldShow: Boolean) {
        _binding?.let { safeBinding ->
            if (shouldShow) {
                safeBinding.progressBar.show()

            } else {
                safeBinding.progressBar.hide()
            }
        } ?: Log.e(TAG, "Binding is null in toggleProgressBar")
    }



    private fun getAllDoctorsAndSpecializations() {
        toggleProgressBar(true)
        val doctorDatabaseRef = FirebaseDatabase.getInstance().getReference("doctors")
        val specializationDatabaseRef =
            FirebaseDatabase.getInstance().getReference("specializations")
        val combinedData = mutableListOf<List<Any>>()

        // Flags to track completion of each database fetch
        val specializationLoaded = AtomicBoolean(false)
        val topRatedDoctorsLoaded = AtomicBoolean(false)
        val allDoctorsLoaded = AtomicBoolean(false)

        // Function to update adapter only if all data is loaded
        fun updateIfAllDataLoaded() {
            if (specializationLoaded.get() && topRatedDoctorsLoaded.get() && allDoctorsLoaded.get()) {
                val cashFlow = listOf(TextItem("Feature"))
                combinedData.add(cashFlow)
                updateAdapterData(combinedData)
            }
        }

        // Load all specializations
        specializationDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allSpecializations = mutableListOf<Specialization>()
                for (specializationSnapshot in dataSnapshot.children) {
                    specializationSnapshot.key?.let { specializationName ->
                        val drawableResId = getDrawableResourceForSpecialization(specializationName)
                        if (drawableResId != null) {
                            allSpecializations.add(
                                Specialization(
                                    specializationName,
                                    drawableResId
                                )
                            )
                        }
                    }
                }
                combinedData.add(0, allSpecializations) // Add specializations at index 0
                specializationLoaded.set(true)
                updateIfAllDataLoaded()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error loading specializations: ${databaseError.message}")
            }
        })

        // Load top 5 doctors by highest averageRating
        doctorDatabaseRef.orderByChild("averageRating").limitToLast(5)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val topRatedDoctors =
                        snapshot.children.mapNotNull { it.getValue(Doctor::class.java) }
                            .sortedByDescending { doctor -> doctor.average_rating }
                            .map { TopRatedDoctor(it) }
                    combinedData.add(topRatedDoctors)
                    topRatedDoctorsLoaded.set(true)
                    updateIfAllDataLoaded()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching top-rated doctors: ${error.message}")
                }
            })

        // Load all doctors for recent listings
        doctorDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allDoctors = mutableListOf<Doctor>()
                for (doctorSnapshot in dataSnapshot.children) {
                    val doctor = doctorSnapshot.getValue(Doctor::class.java)
                    doctor?.let { allDoctors.add(it) }
                }
                combinedData.add(allDoctors)
                allDoctorsLoaded.set(true)
                updateIfAllDataLoaded()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error loading doctors: ${databaseError.message}")
            }
        })

    }

    // Optional logging for combined data

    private fun updateAdapterData(newData: List<List<Any>>) {
        if (parentAdapter != null) {
            parentAdapter?.updateData(newData)
        }
        toggleProgressBar(false)
    }



    // Helper function to map specialization names to drawable resource IDs
    private fun getDrawableResourceForSpecialization(specialization: String): Int? {
        return when (specialization) {
            "Cardiology" -> R.drawable.cardiology
            "Dermatology" -> R.drawable.dermatology
            "Pediatrics" -> R.drawable.pediatrics
            "Orthopedics" -> R.drawable.orthopedic
            "Neurology" -> R.drawable.neurology
            "Gynecology" -> R.drawable.gynecology
            "Ophthalmology" -> R.drawable.ophthalmology
            "Psychiatry" -> R.drawable.psychiatry
            "ENT" -> R.drawable.ent
            "Radiology" -> R.drawable.radiology
            "Gastroenterology" -> R.drawable.gastroenterology
            "Oncology" -> R.drawable.oncology
            else -> null // Handle unknown specializations
        }
    }


    private fun setupObject() {
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initialAdapter()
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    private fun initialAdapter() {
        parentAdapter =
            ParentAdapter(emptyList<List<Any>>().toMutableList(), screenWidth, this@HomeFragment)
        binding.recyclerView.adapter = parentAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener();

        getAllDoctorsAndSpecializations()

    }

    private fun setupListener() {
        binding.btnLogout.setOnClickListener {
            signOut()
        }
        binding.searchBar.setOnClickListener {
            // Find the BottomNavigationView and programmatically select the next menu item
            val bottomNavigationView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView?.selectedItemId = R.id.findBookFragment
        }

    }


    private fun getDisplayMetrics() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Sign out the current user
     */
    private fun signOut() {
        auth.signOut()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_notification -> {
                if (isUserAnonymous()) {
                    notifyUserToLogin()
                }
                true
            }

            R.id.action_account -> {
                if (isUserAnonymous()) {
                    notifyUserToLogin()
                }else{

                    findNavController().navigate(R.id.action_homeFragment_to_accountSettingsFragment)
                }
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onDoctorClick(doctor: Doctor) {

    }

    override fun onTopDoctorClick(doctor: TopRatedDoctor) {

    }


    override fun onSpecializationClick(
        tradeContact: Specialization,
        colorHexCode: String
    ) {
        val bundle = Bundle().apply {
            putString("specializationName", tradeContact.name)
            putString("colorHexCode", colorHexCode)
            putInt("imageResource", tradeContact.imageResId)
        }
        findNavController().navigate(
            R.id.action_homeFragment_to_specializationDoctorsFragment,
            bundle)
    }


    override fun onViewAllDoctorsClick() {

    }

    override fun onViewAllTopRatedDoctorsClick() {

    }

    override fun onViewAllSpecializationsClick() {

    }

    override fun onAddToCartClick(doctor: Doctor) {
        if (isUserAnonymous()) {
            notifyUserToLogin()
            return
        }
        val appointment = createAppointmentForDoctor(doctor)
        val patientId = getCurrentUserId()

       // addAppointmentToPatient(patientId, appointment)
        addDoctorToSavedList(patientId, doctor.doctor_id)
    }
    private fun isUserAnonymous(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.isAnonymous == true
    }

    private fun notifyUserToLogin() {
      findNavController().navigate(R.id.action_homeFragment_to_backAuthFragment)

    }
    private fun createAppointmentForDoctor(doctor: Doctor): Appointments {
        return Appointments(
            appointment_id = "appointment_${System.currentTimeMillis()}",
            doctor_id = doctor.doctor_id,
            date = DateUtils.getCurrentDate(),
            time = DateUtils.getCurrentTime(),
            status = "Upcoming",
            rating = null
        )
    }

    private fun addAppointmentToPatient(patientId: String, appointment: Appointments) {
        val appointmentsRef = FirebaseDatabase.getInstance()
            .getReference("patients")
            .child(patientId)
            .child("appointments")

        appointmentsRef.push().setValue(appointment)
            .addOnSuccessListener {
                Log.d("AddAppointment", "Appointment added successfully for patient: $patientId")
            }
            .addOnFailureListener { error ->
                Log.e("AddAppointment", "Error adding appointment: ${error.message}")
            }
    }
    private fun addDoctorToSavedList(patientId: String, doctorId: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val savedDoctorsRef = databaseRef.child("patients").child(patientId).child("saved_doctors")

        // Step 1: Fetch the current list of saved doctors
        savedDoctorsRef.get().addOnSuccessListener { snapshot ->
            val savedDoctorsList = snapshot.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()

            // Step 2: Check if the doctor is already in the list
            if (!savedDoctorsList.contains(doctorId)) {
                val updatedList = savedDoctorsList + doctorId  // Add the new doctor ID to the list

                // Step 3: Update the saved_doctors field in the database
                savedDoctorsRef.setValue(updatedList)
                    .addOnSuccessListener {
                        Log.d("AddBookmark", "Doctor ID $doctorId added to saved list for patient: $patientId")
                        Toast.makeText(requireContext(), "Doctor added to saved list.", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { error ->
                        Log.e("AddBookmark", "Error adding doctor to saved list: ${error.message}")
                    }
            } else {
                Log.d("AddBookmark", "Doctor is already in saved list.")
                Toast.makeText(requireContext(), "Doctor is already in saved list.", Toast.LENGTH_SHORT).show()

            }
        }.addOnFailureListener { error ->
            Log.e("AddBookmark", "Error fetching saved doctors list: ${error.message}")
        }
    }
    

    override fun onAddToCartClick(topDoctor: TopRatedDoctor) {
        if (isUserAnonymous()) {
            notifyUserToLogin()
            return
        }
        val doctor = topDoctor.doctor
        val appointment = createAppointmentForDoctor(doctor)
        val patientId = getCurrentUserId()

     //   addAppointmentToPatient(patientId, appointment)
        addDoctorToSavedList(patientId, doctor.doctor_id)
    }

    private fun getCurrentUserId(): String {
        
        return FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }
}
