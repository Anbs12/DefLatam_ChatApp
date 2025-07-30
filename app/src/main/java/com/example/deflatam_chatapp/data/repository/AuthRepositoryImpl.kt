package com.example.deflatam_chatapp.data.repository


import com.example.deflatam_chatapp.data.datasource.local.OfflineDataSource
import com.example.deflatam_chatapp.data.datasource.remote.AuthRemoteDataSource
import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implementación concreta de [AuthRepository] que maneja la autenticación y los datos del usuario.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val offlineDataSource: OfflineDataSource
) : AuthRepository {

    /**
     * Registra un nuevo usuario y lo guarda localmente.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @param username Nombre de usuario.
     * @return El [User] registrado.
     */
    override suspend fun registerUser(email: String, password: String, username: String): User {
        val user = authRemoteDataSource.registerUser(email, password, username)
        offlineDataSource.saveUser(user) // Guarda en Room para acceso offline
        return user
    }

    /**
     * Inicia sesión con un usuario y lo guarda localmente.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @return El [User] autenticado.
     */
    override suspend fun loginUser(email: String, password: String): User {
        val user = authRemoteDataSource.loginUser(email, password)
        offlineDataSource.saveUser(user) // Guarda en Room para acceso offline
        return user
    }

    /**
     * Cierra la sesión del usuario actual y limpia los datos locales.
     */
    override suspend fun logoutUser() {
        authRemoteDataSource.logoutUser()
        offlineDataSource.clearAllData() // Limpia todos los datos locales al cerrar sesión.
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     * Primero intenta obtenerlo remotamente, si no, lo busca localmente.
     * @return El [User] actual o null.
     */
    override suspend fun getCurrentUser(): User? {
        val firebaseUser = authRemoteDataSource.getCurrentFirebaseUser()
        return if (firebaseUser != null) {
            // Si hay un usuario en Firebase, intenta obtenerlo de Firestore
            // Si no está en Firestore, podría ser un problema de sincronización o un nuevo usuario.
            // Por ahora, asumimos que si está en Firebase Auth, existe en Firestore o se creará pronto.
            authRemoteDataSource.getUserById(firebaseUser.uid) ?: offlineDataSource.getUserById(firebaseUser.uid)
        } else {
            offlineDataSource.getCurrentUser() // Intenta obtener el último usuario guardado localmente
        }
    }
}
