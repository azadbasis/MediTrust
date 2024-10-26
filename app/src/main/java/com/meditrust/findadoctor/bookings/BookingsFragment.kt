package com.meditrust.findadoctor.bookings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.FragmentBookingsBinding
import com.meditrust.findadoctor.findandbook.FindBookDoctorsAdapter
import com.meditrust.findadoctor.profile.data.model.Doctor
import com.meditrust.findadoctor.profile.data.model.Patient


class BookingsFragment : Fragment() {
    private val TAG = "BookingsFragment"
    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookedDoctorsAdapter: BookedDoctorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookedDoctorsAdapter = BookedDoctorsAdapter()
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
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
       /* if (isUserAnonymous()){
            findNavController().navigate(R.id.action_bookingsFragment_to_backAuthFragment)
        }*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDoctorList.apply {
            val numberOfColumns = 2
            // Use a GridLayoutManager instead of LinearLayoutManager
            layoutManager = GridLayoutManager(context, numberOfColumns)
            adapter = bookedDoctorsAdapter
        }
        val patientId = getCurrentUserId()
        fetchBookmarkedDoctors(patientId)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchBookmarkedDoctors(patientId: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val patientRef = databaseRef.child("patients").child(patientId)

        patientRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val patientSnapshot = task.result
                val savedDoctorsList = patientSnapshot?.child("saved_doctors")
                    ?.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()

                if (savedDoctorsList.isEmpty()) {
                    Log.d(TAG, "No bookmarked doctors found for this patient.")
                    return@addOnCompleteListener
                }

                val bookmarkedDoctors = mutableListOf<Doctor>()
                savedDoctorsList.forEach { doctorId ->
                    databaseRef.child("doctors").child(doctorId).get()
                        .addOnSuccessListener { doctorSnapshot ->
                            doctorSnapshot.getValue(Doctor::class.java)?.let { doctor ->
                                bookmarkedDoctors.add(doctor)
                                displayBookmarkedDoctors(bookmarkedDoctors)
                            }
                        }
                        .addOnFailureListener { error ->
                            Log.e(TAG, "Error fetching doctor data: ${error.message}")
                        }
                }
            } else {
                Log.e(TAG, "Error fetching patient data: ${task.exception?.message}")
            }
        }
    }



    // Function to display bookmarked doctors in the UI (or process them as needed)
    private fun displayBookmarkedDoctors(bookmarkedDoctors: List<Doctor>) {
        // Update your RecyclerView or UI component with bookmarkedDoctors data
        Log.d("BookmarkedDoctors", "Bookmarked doctors: $bookmarkedDoctors")
        // Example: adapter.submitList(bookmarkedDoctors)
        bookedDoctorsAdapter.submitList(bookmarkedDoctors)
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }
    private fun isUserAnonymous(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.isAnonymous == true
    }
}

