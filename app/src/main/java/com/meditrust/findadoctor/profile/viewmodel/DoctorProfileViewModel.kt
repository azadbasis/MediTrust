package com.meditrust.findadoctor.profile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.meditrust.findadoctor.profile.data.model.Doctor

class DoctorProfileViewModel : ViewModel() {

    private val _doctorData = MutableLiveData<Doctor?>()
    val doctorData: LiveData<Doctor?> = _doctorData


    // Function to load doctor data from Firebase
    fun loadDoctorData(uid: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val doctorRef = databaseRef.child("doctors").child(uid)

        doctorRef.get().addOnSuccessListener { snapshot ->
            val doctor = snapshot.getValue(Doctor::class.java)
            _doctorData.value = doctor
        }.addOnFailureListener { exception ->
            Log.d("DoctorProfileViewModel", "Error fetching doctor data", exception)
            _doctorData.value = null // Handle error case
        }
    }

}
