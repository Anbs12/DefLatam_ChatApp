package com.example.deflatam_chatapp.domain.repository

import com.example.deflatam_chatapp.domain.model.User


/**
 * Interfaz para las operaciones de autenticación de usuarios.
 */
interface AuthRepository {
    /**
     * Inicia sesión de un usuario con correo electrónico y contraseña.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return El objeto [User] autenticado si la operación es exitosa.
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Registra un nuevo usuario con correo electrónico y contraseña.
     * @param username Nombre de usuario.
     * @param email Correo electrónico del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     * @return El objeto [User] registrado si la operación es exitosa.
     */
    suspend fun register(username: String, email: String, password: String): Result<User>

    /**
     * Cierra la sesión del usuario actual.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Obtiene el usuario actualmente autenticado.
     * @return El objeto [User] actualmente autenticado o null si no hay ninguno.
     */
    fun getCurrentUser(): User?
}
