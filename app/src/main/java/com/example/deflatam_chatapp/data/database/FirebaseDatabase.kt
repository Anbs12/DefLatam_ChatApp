package com.example.deflatam_chatapp.data.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

/**
 * Provee instancias de los servicios de Firebase.
 */
object FirebaseDatabase {
    /**
     * Retorna la instancia de FirebaseAuth.
     * @return FirebaseAuth instance.
     */
    fun getAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Retorna la instancia de FirebaseFirestore.
     * @return FirebaseFirestore instance.
     */
    fun getFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Retorna la instancia de FirebaseStorage.
     * @return FirebaseStorage instance.
     */
    fun getStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    /**
     * Retorna la instancia de FirebaseMessaging.
     * @return FirebaseMessaging instance.
     */
    fun getMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}

