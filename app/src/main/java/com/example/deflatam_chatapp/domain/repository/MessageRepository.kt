package com.example.deflatam_chatapp.domain.repository

import android.net.Uri
import com.example.deflatam_chatapp.domain.model.Message
import kotlinx.coroutines.flow.Flow


/**
 * Interfaz de repositorio para las operaciones relacionadas con los mensajes de chat.
 */
interface MessageRepository {
    /**
     * Obtiene un flujo de mensajes para una sala de chat específica.
     * @param roomId El ID de la sala de chat.
     * @return Un [Flow] que emite una lista de [Message].
     */
    fun getMessages(roomId: String): Flow<List<Message>>

    /**
     * Envía un nuevo mensaje a una sala de chat.
     * @param message El mensaje a enviar.
     */
    suspend fun sendMessage(message: Message)

    /**
     * Sube un archivo y envía un mensaje que referencia ese archivo.
     * @param roomId El ID de la sala de chat.
     * @param senderId El ID del remitente.
     * @param fileUri La URI local del archivo a subir.
     * @param fileType El tipo de archivo (ej. IMAGE, FILE).
     * @param fileName El nombre del archivo.
     * @return El mensaje creado y enviado con la referencia al archivo.
     */
    suspend fun sendFile(
        roomId: String,
        senderId: String,
        fileUri: Uri,
        fileType: Message.MessageType,
        fileName: String
    ): Message

    /**
     * Actualiza el estado de un mensaje (ej. enviado, entregado, leído).
     * @param roomId El ID de la sala de chat.
     * @param messageId El ID del mensaje a actualizar.
     * @param newStatus El nuevo estado del mensaje.
     */
    suspend fun updateMessageStatus(
        roomId: String,
        messageId: String,
        newStatus: Message.MessageStatus
    )
}

