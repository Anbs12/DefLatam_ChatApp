package com.example.deflatam_chatapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


/**
 * Representa un mensaje en una sala de chat.
 */
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String = UUID.randomUUID()
        .toString(), // ID único del mensaje, también clave primaria de Room.
    val roomId: String, // ID de la sala a la que pertenece el mensaje.
    val senderId: String, // ID del remitente del mensaje.
    val content: String, // Contenido del mensaje de texto.
    val timestamp: Long, // Marca de tiempo del mensaje.
    val type: MessageType, // Tipo de mensaje (TEXT, IMAGE, FILE).
    val fileUrl: String? = null, // URL del archivo adjunto (si el tipo es IMAGE o FILE).
    val fileName: String? = null, // Nombre del archivo adjunto (si el tipo es IMAGE o FILE).
    val status: MessageStatus // Estado del mensaje (SENT, DELIVERED, READ).
) {
    /**
     * Enumera los posibles tipos de mensajes.
     */
    enum class MessageType {
        TEXT, IMAGE, FILE
    }

    /**
     * Enumera los posibles estados de un mensaje.
     */
    enum class MessageStatus {
        SENT, DELIVERED, READ
    }

    // Constructor sin argumentos requerido por Firebase Firestore para deserialización
    constructor() : this("", "", "", "", 0L, MessageType.TEXT, null, null, MessageStatus.SENT)
}

