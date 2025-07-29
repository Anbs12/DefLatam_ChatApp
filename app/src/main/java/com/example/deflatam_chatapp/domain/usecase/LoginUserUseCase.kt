package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.repository.AuthRepository

/**
 * Caso de uso para la lógica de inicio de sesión de un usuario.
 */
class LoginUserUseCase(private val authRepository: AuthRepository) {
    /**
     * Ejecuta la operación de inicio de sesión.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Un [Result] que contiene el objeto [User] autenticado si es exitoso.
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
}