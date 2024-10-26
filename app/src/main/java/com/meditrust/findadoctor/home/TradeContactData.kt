package com.meditrust.findadoctor.home

import android.os.Parcelable

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}

@kotlinx.android.parcel.Parcelize
data class TradeContact(
    val name: String,
    val phone: String = "",
    val gender: Gender = Gender.OTHER,
    val isHandicapped: Boolean = false
) : Parcelable
