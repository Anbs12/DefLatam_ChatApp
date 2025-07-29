package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.repository.AuthRepository
import javax.inject.Inject


/**
 * Caso de uso para registrar un nuevo usuario.
 */
class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Invoca el caso de uso para registrar un nuevo usuario con email y contraseña.
     * @param email El correo electrónico del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     * @return Un [Result] que indica el éxito o fracaso de la operación y el objeto [User] en caso de éxito.
     */
    suspend operator fun invoke(email: String, password: String, userName: String): User {
        return authRepository.registerUser(email, password, userName)
    }
}