package com.example.deflatam_chatapp.data.repository


import com.example.deflatam_chatapp.data.datasource.local.OfflineDataSource
import com.example.deflatam_chatapp.data.datasource.remote.AuthRemoteDataSource
import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Implementación del repositorio de autenticación.
 */
class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val offlineDataSource: OfflineDataSource
) : AuthRepository {

    /**
     * Inicia sesión del usuario.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @return [Result] con el [User] si tiene éxito.
     */
    override suspend fun login(email: String, password: String): Result<User> {
        return authRemoteDataSource.login(email, password).onSuccess { user ->
            offlineDataSource.saveUser(user) // Guarda el usuario en caché local.
        }
    }

    /**
     * Registra un nuevo usuario.
     * @param username Nombre de usuario.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @return [Result] con el [User] si tiene éxito.
     */
    override suspend fun register(username: String, email: String, password: String): Result<User> {
        return authRemoteDataSource.register(username, email, password).onSuccess { user ->
            offlineDataSource.saveUser(user) // Guarda el usuario en caché local.
        }
    }

    /**
     * Cierra la sesión del usuario.
     * @return [Result] de [Unit] si tiene éxito.
     */
    override suspend fun logout(): Result<Unit> {
        return authRemoteDataSource.logout().onSuccess {
            offlineDataSource.clearAllData() // Limpia los datos locales al cerrar sesión.
        }
    }

    /**
     * Obtiene el usuario actualmente autenticado desde la caché o Firebase.
     * @return El [User] actual o `null`.
     */
    override fun getCurrentUser(): User? {
        val firebaseUser = authRemoteDataSource.getCurrentFirebaseUser()
        return firebaseUser?.let {
            // Intentar obtener de la caché primero, si no, crear un objeto User básico.
            offlineDataSource.getUserById(it.uid) ?: User(it.uid, it.displayName ?: "Desconocido", it.email ?: "")
        }
    }
}
