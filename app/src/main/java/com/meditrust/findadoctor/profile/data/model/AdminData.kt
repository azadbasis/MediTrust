package com.meditrust.findadoctor.profile.data.model

data class Admin(
    val admin_id: String = "",
    val user_role: String = "",
    val name: String = "",
    val email: String = "",
    val phone_number: String = "",
    val profile_image: String = "", // Optional field for storing profile image URL
    val address: String = "",
)