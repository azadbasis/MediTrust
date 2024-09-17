package com.meditrust.findadoctor.auth.presentation.login.state

sealed class LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()
    object Submit : LoginUiEvent()
}
