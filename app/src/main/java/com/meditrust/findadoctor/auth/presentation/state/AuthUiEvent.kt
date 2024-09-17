package com.meditrust.findadoctor.auth.presentation.state

sealed class AuthUiEvent {
    data class EmailChanged(val email: String) : AuthUiEvent()
    data class PasswordChanged(val password: String) : AuthUiEvent()
    object Login : AuthUiEvent()
    object Register : AuthUiEvent()
}