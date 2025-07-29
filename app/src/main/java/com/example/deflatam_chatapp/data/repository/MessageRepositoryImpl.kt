package com.example.deflatam_chatapp.data.repository

import android.net.Uri
import com.example.deflatam_chatapp.data.datasource.local.OfflineDataSource
import com.example.deflatam_chatapp.data.datasource.remote.MessageRemoteDataSource
import com.example.deflatam_chatapp.data.datasource.websocket.WebSocketManager
import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject
import androidx.core.net.toUri

/**
 * Implementación del repositorio de mensajes.
 */
class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val webSocketManager: WebSocketManager,
    private val offlineDataSource: OfflineDataSource
) : MessageRepository {

    /**
     * Envía un nuevo mensaje.
     * También lo envía por WebSocket para tiempo real.
     * @param message El mensaje a enviar.
     * @return [Result] con el [Message] enviado.
     */
    override suspend fun sendMessage(message: Message): Result<Message> {
        // Enviar a Firestore para persistencia.
        val remoteResult = messageRemoteDataSource.sendMessage(message)
        remoteResult.onSuccess { sentMessage ->
            offlineDataSource.addMessage(sentMessage) // Agrega a la caché local.
            // Enviar por WebSocket para actualización en tiempo real a otros clientes.
            webSocketManager.sendWebSocketMessage(sentMessage)
        }
        return remoteResult
    }

    /**
     * Obtiene un flujo de mensajes para una sala.
     * Combina mensajes de Firestore (histórico) y WebSocket (tiempo real).
     * @param roomId El ID de la sala.
     * @return [Flow] de [Result] con lista de [Message].
     */
    override fun getMessages(roomId: String): Flow<Result<List<Message>>> {
        // Observar mensajes desde Firestore para el historial.
        val firestoreMessages = messageRemoteDataSource.getMessages(roomId).onEach { result ->
            result.onSuccess { messages ->
                offlineDataSource.saveMessages(roomId, messages) // Sincroniza con la caché local.
            }
        }

        // Observar mensajes desde el WebSocket para tiempo real.
        val websocketFlow = webSocketManager.incomingMessages
            .onEach { message ->
                if (message.roomId == roomId) {
                    offlineDataSource.addMessage(message) // Añade mensajes de WebSocket a la caché.
                }
            }
            .map {
                Result.success(
                    offlineDataSource.getMessages(roomId)
                )
            }

        return merge(firestoreMessages, websocketFlow) as Flow<Result<List<Message>>>
    }

    /**
     * Actualiza el estado de un mensaje.
     * @param messageId ID del mensaje.
     * @param status Nuevo estado del mensaje.
     * @return [Result] de [Unit] si tiene éxito.
     */
    override suspend fun updateMessageStatus(messageId: String, status: String): Result<Unit> {
        return messageRemoteDataSource.updateMessageStatus(messageId, status).onSuccess {
            offlineDataSource.updateMessageStatus(
                messageId,
                status
            ) // Actualiza el estado en la caché.
        }
    }

    /**
     * Envía un archivo o imagen.
     * Sube el archivo a Firebase Storage y luego envía un mensaje con la URL.
     * @param roomId ID de la sala.
     * @param senderId ID del remitente.
     * @param fileUri URI local del archivo.
     * @param fileName Nombre del archivo.
     * @param messageType Tipo de mensaje.
     * @return [Result] con el [Message] enviado.
     */
    override suspend fun sendFile(
        roomId: String,
        senderId: String,
        fileUri: String,
        fileName: String,
        messageType: Message.MessageType
    ): Result<Message> {
        val uri = fileUri.toUri()
        return messageRemoteDataSource.uploadFile(uri, fileName, messageType).fold(
            onSuccess = { fileUrl ->
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderId = senderId,
                    content = if (messageType == Message.MessageType.IMAGE) "Imagen enviada" else "Archivo enviado",
                    timestamp = System.currentTimeMillis(),
                    type = messageType,
                    fileUrl = fileUrl,
                    fileName = fileName,
                    status = Message.MessageStatus.SENT
                )
                sendMessage(message) // Envía el mensaje con la URL del archivo.
            },
            onFailure = { it: Throwable ->
                Result.failure(it) // Propaga el error de subida de archivo.
            }
        )
    }

    /**
     * Observa mensajes en tiempo real desde el WebSocket.
     * @param roomId El ID de la sala para la cual escuchar mensajes.
     * @return Un [Flow] de [Message] que se reciben en tiempo real.
     */
    override fun observeWebSocketMessages(roomId: String): Flow<Message> {
        // Asegúrate de que el WebSocket esté conectado a la URL correcta.
        // Normalmente, la conexión se gestionaría en un punto más alto (ej. Application o ViewModel)
        // para que sea persistente durante la vida de la app o de la sala de chat.
        // Por simplicidad, aquí solo retornamos el flujo existente.
        return webSocketManager.incomingMessages
            .onEach { message ->
                if (message.roomId == roomId) {
                    offlineDataSource.addMessage(message) // Asegurar que los mensajes de WS se añadan a la caché.
                }
            }
    }
}
