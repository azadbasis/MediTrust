package com.meditrust.findadoctor.account.presentation.viewmodel.enums


import android.content.Context
import com.meditrust.findadoctor.R

enum class ProfileStatus(
    val titleResId: Int,
    val messageResId: Int
) {
    INCOMPLETE(
        titleResId = R.string.profile_incomplete_title,
        messageResId = R.string.profile_incomplete_message
    ),
    PENDING_REVIEW(
        titleResId = R.string.profile_pending_review_title,
        messageResId = R.string.profile_pending_review_message
    ),
    ACTIVATED(
        titleResId = R.string.profile_activated_title,
        messageResId = R.string.profile_activated_message
    ),
    ADMIN_ACTIVATED(
        titleResId = R.string.admin_activated_title,
        messageResId = R.string.admin_activated_message
    ),
    PATIENT_ACTIVATED(
        titleResId = R.string.patient_activated_title,
        messageResId = R.string.patient_activated_message
    ),
    PATIENT_RESTRICTED(
        titleResId = R.string.patient_restricted_title,
        messageResId = R.string.patient_restricted_message
    );

    fun getTitle(context: Context): String = context.getString(titleResId)

    fun getMessage(context: Context): String = context.getString(messageResId)
}
