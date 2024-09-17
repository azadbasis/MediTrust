package com.meditrust.findadoctor.auth.domain.usecase

import com.meditrust.findadoctor.auth.domain.model.User
import com.meditrust.findadoctor.auth.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    /* Use cases for authentication (e.g., LoginUseCase, RegisterUseCase).*/
    suspend fun execute(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }

}