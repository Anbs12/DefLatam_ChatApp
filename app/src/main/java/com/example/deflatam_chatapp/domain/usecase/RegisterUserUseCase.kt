package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.repository.AuthRepository


/**
 * Caso de uso para la lógica de registro de un nuevo usuario.
 */
class RegisterUserUseCase(private val authRepository: AuthRepository) {
    /**
     * Ejecuta la operación de registro de usuario.
     * @param username Nombre de usuario.
     * @param email Correo electrónico del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     * @return Un [Result] que contiene el objeto [User] registrado si es exitoso.
     */
    suspend operator fun invoke(username: String, email: String, password: String): Result<User> {
        return authRepository.register(username, email, password)
    }
}