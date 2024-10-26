package com.meditrust.findadoctor.profile.data.model

import com.meditrust.findadoctor.core.util.UserRoles

data class Doctor(
    val doctor_id: String = "",
    val user_role: String= UserRoles.DOCTOR,
    val title: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",                         // Doctor's contact number
    val dob: String = "",                         // Doctor's contact number
    val specialization: String = "",                // Doctor's specialization
    val profile_image: String = "",
    val doctor_type: String = "",
    val medical_license_number: String = "",
    val experience: String = "0",                   // Years of experience
    val gender: String = "0",                   // Years of experience
    val clinic_name: String = "",                // Clinic address
    val clinic_address: String = "",                // Clinic address
    val clinic_contact: String = "",                // Clinic's contact number
    val availability: String = "",                  // Doctor's availability hours
    val total_rating: String = "0",                 // Sum of all review ratings
    val ratings_count: String = "0",                // Number of reviews
    val average_rating: String = "0.0",             // Pre-calculated average rating
    val review_score: String = "0.0",               // Optional: Weighted rating for review sorting
    val appointment_fee: String = "$0",             // Consultation fee
    val in_person: String = "false",                // Type of consultation (in-person)
    val online: String = "false",                   // Type of consultation (online)
    val languages_spoken: List<String> = emptyList(),  // Languages spoken
    val certifications: List<String> = emptyList(), // Certifications or credentials
    val degrees: String = "",
    val about_me: String = "",
    val verified: String = "false",                 // Verification status
    val medical_school: String = "",
    val graduation_year: String = "",
    val reviews: Map<String, Review> = emptyMap(),  // Doctor reviews
    val appointments: Map<String, Appointment> = emptyMap()  // Doctor appointments
)

data class Review(
    val rating: String = "0",                       // Rating given by the patient
    val comment: String = "",                       // Patient's comment
    val reviewed_by: String = "",                   // Patient who submitted the review
    val date: String = ""                           // Date of the review
)

data class Appointment(
    val patient_id: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = ""                         // Status of the appointment (e.g., Confirmed)
)


