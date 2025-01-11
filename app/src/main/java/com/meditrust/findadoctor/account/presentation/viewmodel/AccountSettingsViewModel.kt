package com.meditrust.findadoctor.account.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meditrust.findadoctor.account.presentation.viewmodel.enums.ProfileStatus

class AccountSettingsViewModel : ViewModel() {
    val profileStatus: LiveData<ProfileStatus> = MutableLiveData(ProfileStatus.INCOMPLETE)

    fun getStatusTitle(context: Context): String {
        return profileStatus.value?.getTitle(context) ?: ""
    }

    fun getStatusMessage(context: Context): String {
        return profileStatus.value?.getMessage(context) ?: ""
    }
}