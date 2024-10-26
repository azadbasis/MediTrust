package com.meditrust.findadoctor.auth.data.model

data class User(
    var user_id: String = "",
    var name: String = "",
    var phone: String = "1",
    var profile_image: String = "",
    var security_level: String = "1"
)

