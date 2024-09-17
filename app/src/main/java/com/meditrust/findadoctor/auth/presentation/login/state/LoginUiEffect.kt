package com.meditrust.findadoctor.auth.presentation.login.state

sealed class LoginUiEffect {
    object NavigateToHome : LoginUiEffect()
    data class ShowToast(val message: String) : LoginUiEffect()
}
