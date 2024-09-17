package com.meditrust.findadoctor.auth.presentation.login.state

data class LoginViewState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null
)

