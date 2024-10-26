package com.meditrust.findadoctor.profile.data.model



data class Patient(
    val patient_id: String = "",
    val user_role: String = "",
    val name: String = "",
    val email: String = "",
    val phone_number: String = "",
    val profile_image: String = "", // Optional field for storing profile image URL
    val address: String = "",
    val medical_history: String = "", // Optional field for storing medical history
    val saved_doctors: MutableList<String> = mutableListOf(),
    val appointments: MutableList<Appointments> = mutableListOf(), // List of past and upcoming appointments
)



data class Appointments(
    val appointment_id: String = "",
    val doctor_id: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "",
    val rating: String? = null
)
