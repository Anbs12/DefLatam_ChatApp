package com.example.deflatam_chatapp.presentation.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.deflatam_chatapp.R

/**
 * Actividad para la interfaz de chat en tiempo real dentro de una sala específica.
 */
class ChatActivity : AppCompatActivity() {
    /**
     * Se llama cuando la actividad se crea por primera vez.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        // Aquí se inicializarán los elementos de la UI del chat.
    }
}