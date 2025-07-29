package com.example.deflatam_chatapp.data.datasource.remote

import com.example.deflatam_chatapp.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 * Fuente de datos remota para las operaciones de autenticación.
 */
class AuthRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    /**
     * Inicia sesión con email y contraseña.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return El objeto [User] autenticado si la operación es exitosa.
     */
    suspend fun login(email: String, password: String): Result<User> = runCatching {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: throw Exception("User ID not found after login")
        val userDoc = firestore.collection("users").document(userId).get().await()
        userDoc.toObject(User::class.java) ?: throw Exception("User data not found in Firestore")
    }

    /**
     * Registra un nuevo usuario con email y contraseña.
     * @param username Nombre de usuario.
     * @param email Correo electrónico del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     * @return El objeto [User] registrado si la operación es exitosa.
     */
    suspend fun register(username: String, email: String, password: String): Result<User> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid ?: throw Exception("User ID not found after registration")
        val newUser = User(id = userId, username = username, email = email)
        firestore.collection("users").document(userId).set(newUser).await()
        newUser
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    suspend fun logout(): Result<Unit> = runCatching {
        firebaseAuth.signOut()
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     * @return El objeto [User] actualmente autenticado o null si no hay ninguno.
     */
    fun getCurrentFirebaseUser(): com.google.firebase.auth.FirebaseUser? {
        return firebaseAuth.currentUser
    }

    /**
     * Obtiene los datos del usuario desde Firestore.
     * @param userId ID del usuario.
     * @return Objeto [User] si se encuentra, null de lo contrario.
     */
    suspend fun getUserData(userId: String): User? {
        return try {
            firestore.collection("users").document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
