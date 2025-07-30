package com.example.deflatam_chatapp.domain.repository

import com.example.deflatam_chatapp.domain.model.User


/**
 * Interfaz de repositorio para las operaciones de autenticación de usuarios.
 */
interface AuthRepository {
    /**
     * Registra un nuevo usuario con los detalles proporcionados.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param username Nombre de usuario.
     * @return El [User] recién registrado.
     */
    suspend fun registerUser(email: String, password: String, username: String): User

    /**
     * Inicia sesión con las credenciales del usuario.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return El [User] autenticado.
     */
    suspend fun loginUser(email: String, password: String): User

    /**
     * Cierra la sesión del usuario actual.
     */
    suspend fun logoutUser()

    /**
     * Obtiene el usuario actualmente autenticado.
     * @return El [User] actualmente autenticado, o null si no hay ninguno.
     */
    suspend fun getCurrentUser(): User?
}

