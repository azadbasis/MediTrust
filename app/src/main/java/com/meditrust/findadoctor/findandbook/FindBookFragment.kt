package com.meditrust.findadoctor.findandbook

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.FragmentFindBookBinding
import com.meditrust.findadoctor.profile.data.model.Doctor
import kotlin.collections.plus


class FindBookFragment : Fragment(), FindBookDoctorsAdapter.OnDoctorClickListener {

    private val TAG = "FindBookFragment"
    private var _binding: FragmentFindBookBinding? = null
    private val binding get() = _binding!!


    private lateinit var findBookDoctorsAdapter: FindBookDoctorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val navController = findNavController()
        val navigationBackHandler = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    requireActivity().finish()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, navigationBackHandler)

        findBookDoctorsAdapter = FindBookDoctorsAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindBookBinding.inflate(inflater, container, false)
        binding.apply {
            (activity as AppCompatActivity).setSupportActionBar(productToolbar)
           // (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

            doctorAppBar.statusBarForeground =
                MaterialShapeDrawable.createWithElevationOverlay(context)

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* binding.rvDoctorList.apply {
            val numberOfColumns = 2
            // Use a GridLayoutManager instead of LinearLayoutManager
            layoutManager = GridLayoutManager(context, numberOfColumns)
            adapter = findBookDoctorsAdapter
        }*/
        binding.rvDoctorList.adapter = findBookDoctorsAdapter
        loadAllDoctors()

    }

    private fun loadAllDoctors() {
        val doctorDatabaseRef = FirebaseDatabase.getInstance().getReference("doctors")

        // Load all doctors for recent listings
        doctorDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allDoctors = mutableListOf<Doctor>()
                for (doctorSnapshot in dataSnapshot.children) {
                    val doctor = doctorSnapshot.getValue(Doctor::class.java)
                    doctor?.let { allDoctors.add(it) }
                }
                updateIfAllDataLoaded(allDoctors)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error loading doctors: ${databaseError.message}")
            }
        })
    }

    private fun updateIfAllDataLoaded(doctors: MutableList<Doctor>) {
        findBookDoctorsAdapter.submitList(doctors)
        if (_binding != null) {
            if (doctors.size == 1) {
                binding.tvCaption.text = "${doctors.size} Doctor - Recently listed"
            } else {
                binding.tvCaption.text = "${doctors.size} Doctors - Recently listed"
            }
        } else {
            Log.e(TAG, "Binding is null in updateIfAllDataLoaded")
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_specialization_doctors_filter, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in doctors"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query change
                findBookDoctorsAdapter.filter(newText)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDoctorClick(doctor: Doctor) {
        if (isUserAnonymous()) {
            notifyUserToLogin()
            return
        }
        val patientId = getCurrentUserId()
        addDoctorToSavedList(patientId, doctor.doctor_id)
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

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }
    private fun isUserAnonymous(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.isAnonymous == true
    }

    private fun notifyUserToLogin() {
       findNavController().navigate(R.id.action_findBookFragment_to_backAuthFragment)
    }
}
