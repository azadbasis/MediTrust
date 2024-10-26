package com.meditrust.findadoctor.auth.domain.repository

import com.meditrust.findadoctor.auth.domain.model.User


interface AuthRepository {
    /*Interfaces for authentication repositories (e.g., AuthRepository).*/

    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>
}