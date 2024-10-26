package com.meditrust.findadoctor.auth.data.repository

// SpecializationProvider.kt

import kotlin.random.Random

class SpecializationProvider {

    // Predefined list of specializations
    private val SPECIALIZATION_LIST = listOf(
        "Cardiology",
        "Dermatology",
        "Pediatrics",
        "Orthopedics",
        "Neurology",
        "Gynecology",
        "Ophthalmology",
        "Psychiatry",
        "ENT",
        "Radiology",
        "Gastroenterology",
        "Oncology"
    )

    // Helper method to select a random specialization from the list
    fun getRandomSpecialization(): String {
        val randomIndex = Random.nextInt(SPECIALIZATION_LIST.size)
        return SPECIALIZATION_LIST[randomIndex]
    }
}
