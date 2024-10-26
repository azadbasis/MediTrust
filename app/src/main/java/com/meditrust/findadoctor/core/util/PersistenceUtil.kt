package com.meditrust.findadoctor.core.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class PersistenceUtil(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME,MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()


    fun setOnboardingCompleted(onboardingCompleted: Boolean) {
        editor.putBoolean(ONBOARDING_COMPLETED, onboardingCompleted)
        editor.apply()
    }
    fun isOnboardingCompleted(): Boolean {
        return pref.getBoolean(ONBOARDING_COMPLETED, false)
    }
    fun setProfileCompleted(profileCompleted: Boolean) {
        editor.putBoolean(PROFILE_COMPLETED, profileCompleted)
        editor.apply()
    }
    fun isProfileCompleted(): Boolean {
        return pref.getBoolean(PROFILE_COMPLETED, false)
    }

    companion object {
        private const val PREF_NAME = "Medi Trust"
        private const val ONBOARDING_COMPLETED = "onboarding_completed"
        private const val PROFILE_COMPLETED = "profile_completed"

    }
}
