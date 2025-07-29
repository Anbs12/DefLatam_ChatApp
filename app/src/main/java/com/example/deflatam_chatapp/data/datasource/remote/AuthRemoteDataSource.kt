package com.example.deflatam_chatapp.data.datasource.remote

import com.example.deflatam_chatapp.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Fuente de datos remota para la autenticación de usuarios utilizando Firebase Authentication y Firestore.
 */
@Singleton
class AuthRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * Registra un nuevo usuario con correo electrónico y contraseña en Firebase.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param username Nombre de usuario.
     * @return El [User] creado si tiene éxito.
     * @throws Exception Si el registro falla.
     */
    suspend fun registerUser(email: String, password: String, username: String): User {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("User creation failed")
        val user = User(
            id = firebaseUser.uid,
            username = username,
            email = email,
            //avatarUrl = null
        )
        firestore.collection("users").document(user.id).set(user).await()
        return user
    }

    /**
     * Inicia sesión con correo electrónico y contraseña en Firebase.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return El [User] autenticado si tiene éxito.
     * @throws Exception Si el inicio de sesión falla.
     */
    suspend fun loginUser(email: String, password: String): User {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Login failed")
        return getUserById(firebaseUser.uid) ?: throw Exception("User data not found")
    }

    /**
     * Cierra la sesión del usuario actual de Firebase.
     */
    fun logoutUser() {
        firebaseAuth.signOut()
    }

    /**
     * Obtiene el usuario actual autenticado de Firebase.
     * @return El [FirebaseUser] actual, o null si no hay ninguno.
     */
    fun getCurrentFirebaseUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    /**
     * Obtiene los datos de un usuario de Firestore por su ID.
     * @param userId El ID del usuario.
     * @return El [User] si se encuentra, o null de lo contrario.
     */
    suspend fun getUserById(userId: String): User? {
        return firestore.collection("users").document(userId).get().await().toObject(User::class.java)
    }
}

