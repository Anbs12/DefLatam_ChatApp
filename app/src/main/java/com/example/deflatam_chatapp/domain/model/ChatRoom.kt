package com.example.deflatam_chatapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una sala de chat.
 */
@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey val id: String, // ID único de la sala de chat, también clave primaria de Room.
    val name: String, // Nombre de la sala de chat.
    val description: String? = null, // Descripción de la sala de chat (opcional).
    val participants: List<String> = emptyList(), // Lista de IDs de usuarios participantes.
    val createdAt: Long = System.currentTimeMillis() // Marca de tiempo de creación de la sala.
)