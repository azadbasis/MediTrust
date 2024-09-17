package com.meditrust.findadoctor.auth.presentation.state

sealed class AuthUiEffect {
    object NavigateToHome : AuthUiEffect()
    data class ShowToast(val message: String) : AuthUiEffect()
}