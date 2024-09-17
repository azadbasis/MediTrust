package com.meditrust.findadoctor.auth.presentation.state

data class AuthViewState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)