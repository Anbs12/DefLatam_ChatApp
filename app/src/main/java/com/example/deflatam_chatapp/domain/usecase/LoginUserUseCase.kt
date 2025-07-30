package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para autenticar un usuario existente.
 */
class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Invoca el caso de uso para iniciar sesión con credenciales de usuario.
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @return Un [Result] que indica el éxito o fracaso de la operación y el objeto [User] en caso de éxito.
     */
    suspend operator fun invoke(email: String, password: String): User {
        return authRepository.loginUser(email, password)
    }
}