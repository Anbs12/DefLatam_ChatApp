package com.example.deflatam_chatapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase principal de la aplicación, anotada con @HiltAndroidApp para habilitar la inyección de dependencias con Hilt.
 */
@HiltAndroidApp
class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        /*Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )*/

        FirebaseApp.initializeApp(this) // Asegúrate de que Firebase se inicialice primero
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        /*FirebaseApp.initializeApp(this) // Asegúrate de que Firebase se inicialice primero
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        // Utiliza el DebugAppCheckProviderFactory para desarrollo
        // En producción, usarías PlayIntegrityAppCheckProviderFactory
        firebaseAppCheck.installAppCheckProviderFactory (
            DebugAppCheckProviderFactory.getInstance()
            // Para producción, cambia a: PlayIntegrityAppCheckProviderFactory.getInstance()
        )*/
    }
}