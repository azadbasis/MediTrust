package com.meditrust.findadoctor.home.presentation

import Product
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.FragmentSpecializationDoctorsBinding
import com.meditrust.findadoctor.profile.data.model.Doctor
import kotlin.collections.plus


class SpecializationDoctorsFragment : Fragment(), SpecializeDoctorsAdapter.OnProductClickListener {

    private var _binding: FragmentSpecializationDoctorsBinding? = null
    private val binding get() = _binding!!
    private lateinit var specializedAdapter: SpecializeDoctorsAdapter

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

        specializedAdapter = SpecializeDoctorsAdapter(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpecializationDoctorsBinding.inflate(inflater, container, false)

        binding.apply {
            (activity as AppCompatActivity).setSupportActionBar(productToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

            productAppBar.statusBarForeground =
                MaterialShapeDrawable.createWithElevationOverlay(context)

        }


        return binding.root
    }

    lateinit var specializationName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Additional setup can be done here if needed

        specializationName = arguments?.getString("specializationName").orEmpty()

        val colorHexCode = arguments?.getString("colorHexCode")
        val imageResId = arguments?.getInt("imageResource")

        // Fix the issue by properly setting the image resource in the ImageView
        imageResId?.let {
            binding.albumImageView.setImageResource(it)
        }
        binding.tvSpecializationName.text = specializationName
        binding.doctorCollapsingToolbar.title = specializationName
        
        binding.productToolbar.setNavigationOnClickListener {
            // Handle the back button press by navigating to the previous destination
            findNavController().navigateUp()
        }
      /*  binding.rvDoctorList.apply {
            val numberOfColumns = 2
            // Use a GridLayoutManager instead of LinearLayoutManager
            layoutManager = GridLayoutManager(context, numberOfColumns)
            adapter = specializedAdapter
        }*/
        binding.rvDoctorList.adapter = specializedAdapter
        findSpecializedDoctor(specializationName)


        binding.rvDoctorList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scrolling down
                } else {
                    // Scrolling up
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Do something when scroll is at the bottom
                }
            }
        })
    }


    private fun findSpecializedDoctor(specializationName: String?) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("doctors")

        // Query for doctors with specialization "Cardiology"
        val query = databaseRef.orderByChild("specialization").equalTo(specializationName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctors = mutableListOf<Doctor>()
                
                for (doctorSnapshot in dataSnapshot.children) {
                    val doctor = doctorSnapshot.getValue(Doctor::class.java)
                    // Process each doctor profile
                    Log.d("TAG", "onDataChange3: ${doctor?.name}")
                    doctor?.let { doctors.add(it) }
                }
              
                specializedAdapter.submitList(doctors)
                if (doctors.size == 1) {
                    binding.tvCaption.text = "${doctors.size} Doctor - Recently listed"
                } else {
                    binding.tvCaption.text = "${doctors.size} Doctors - Recently listed"
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Log.e("Firebase", "Error: ${databaseError.message}")
            }
        })
    }

    override fun onProductClick(doctor: Doctor) {
        if (isUserAnonymous()) {
            notifyUserToLogin()
            return
        }
        addDoctorToSavedList(getCurrentUserId(), doctor.doctor_id)
    }
    private fun isUserAnonymous(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.isAnonymous == true
    }

    private fun notifyUserToLogin() {
        findNavController().navigate(R.id.action_specializationDoctorsFragment_to_backAuthFragment2)
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
                specializedAdapter.filter(newText)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}
