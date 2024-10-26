package com.meditrust.findadoctor.profile.domain

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class UserProfileManager(private val auth: FirebaseAuth) {

    fun updateUserProfile(name: String?, photoUrl: Uri?) {
        val user = auth.currentUser

        user?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUrl)
                .build()

            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ProfileUpdate", "User profile updated.")
                    } else {
                        Log.e("ProfileUpdate", "Error updating profile", task.exception)
                    }
                }
        }
    }
}