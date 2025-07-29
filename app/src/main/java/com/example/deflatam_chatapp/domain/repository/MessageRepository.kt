package com.example.deflatam_chatapp.domain.repository

import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.model.MessageType
import kotlinx.coroutines.flow.Flow


/**
 * Interfaz para las operaciones relacionadas con los mensajes de chat.
 */
interface MessageRepository {
    /**
     * Envía un nuevo mensaje a una sala de chat.
     * @param message El objeto [Message] a enviar.
     * @return El objeto [Message] enviado si la operación es exitosa.
     */
    suspend fun sendMessage(message: Message): Result<Message>

    /**
     * Obtiene un flujo de mensajes para una sala de chat específica.
     * @param roomId El ID de la sala de chat.
     * @return Un [Flow] que emite una lista de [Message] para la sala.
     */
    fun getMessages(roomId: String): Flow<Result<List<Message>>>

    /**
     * Actualiza el estado de un mensaje (ej. de enviado a entregado/leído).
     * @param messageId El ID del mensaje a actualizar.
     * @param status El nuevo estado del mensaje.
     * @return Un [Result] indicando el éxito o fallo de la operación.
     */
    suspend fun updateMessageStatus(messageId: String, status: String): Result<Unit>

    /**
     * Envía un archivo o imagen.
     * @param roomId El ID de la sala.
     * @param senderId El ID del remitente.
     * @param fileUri URI local del archivo a enviar.
     * @param fileName Nombre del archivo.
     * @param messageType Tipo de mensaje (IMAGE o FILE).
     * @return El objeto [Message] enviado con la URL del archivo.
     */
    suspend fun sendFile(
        roomId: String,
        senderId: String,
        fileUri: String, // Usar un URI de String por ahora, luego podría ser Uri de Android.
        fileName: String,
        messageType: MessageType
    ): Result<Message>

    /**
     * Escucha mensajes en tiempo real a través de WebSockets.
     * @param roomId El ID de la sala para la cual escuchar mensajes.
     * @return Un [Flow] de [Message] que se reciben en tiempo real.
     */
    fun observeWebSocketMessages(roomId: String): Flow<Message>
}
