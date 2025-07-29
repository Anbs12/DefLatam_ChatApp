package com.example.deflatam_chatapp.presentation.ui.salas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.deflatam_chatapp.R

/**
 * Actividad para mostrar la lista de salas de chat disponibles.
 */
class SalasActivity : AppCompatActivity() {
    /**
     * Se llama cuando la actividad se crea por primera vez.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salas)
        // Aquí se inicializarán los elementos de la UI para las salas.
    }
}