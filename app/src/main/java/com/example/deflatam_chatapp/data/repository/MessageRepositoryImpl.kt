package com.example.deflatam_chatapp.data.repository

import android.net.Uri
import com.example.deflatam_chatapp.data.datasource.local.OfflineDataSource
import com.example.deflatam_chatapp.data.datasource.remote.MessageRemoteDataSource
import com.example.deflatam_chatapp.data.datasource.websocket.WebSocketManager
import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.repository.MessageRepository
import com.example.deflatam_chatapp.utils.EncryptionUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implementación concreta de [MessageRepository] que gestiona los mensajes de chat.
 */
@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val webSocketManager: WebSocketManager,
    private val offlineDataSource: OfflineDataSource,
    private val encryptionUtils: EncryptionUtils
) : MessageRepository {

    init {
        webSocketManager.connect() // Asegurarse de que el WebSocket se conecte al inicializar el repositorio
    }

    /**
     * Obtiene un flujo combinado de mensajes de la fuente remota (Firestore) y del WebSocket.
     * También actualiza el caché local con los mensajes.
     * @param roomId El ID de la sala de chat.
     * @return Un [Flow] que emite una lista de [Message].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMessages(roomId: String): Flow<List<Message>> {
        // Combina los mensajes de Firestore y WebSocket, y descifra el contenido.
        return combine(
            messageRemoteDataSource.getMessages(roomId),
            webSocketManager.incomingMessages.map { message ->
                // Solo emitir el mensaje del WebSocket si pertenece a la sala actual
                if (message.roomId == roomId) listOf(message) else emptyList()
            }.distinctUntilChanged().onEach { incomingWsMessages ->
                // Guardar mensajes entrantes del WebSocket en Room
                if (incomingWsMessages.isNotEmpty()) {
                    offlineDataSource.saveMessages(incomingWsMessages)
                }
            },
            offlineDataSource.getMessages(roomId).distinctUntilChanged() // Mensajes desde Room
        ) { remoteMessages, websocketMessages, localMessages ->
            // Prioridad: WebSocket (más reciente) > Remoto > Local
            val combined = (remoteMessages + websocketMessages + localMessages)
                .distinctBy { it.id } // Elimina duplicados por ID
                .sortedBy { it.timestamp } // Ordena por marca de tiempo

            // Descifra el contenido de los mensajes antes de emitirlos.
            combined.map { message ->
                if (message.type == Message.MessageType.TEXT) {
                    message.copy(content = encryptionUtils.decrypt(message.content) ?: message.content)
                } else {
                    message // Los mensajes de archivo/imagen no se descifran aquí.
                }
            }
        }.distinctUntilChanged() // Asegura que solo se emitan cambios reales.
    }

    /**
     * Envía un mensaje. Lo cifra, lo envía a Firestore y a través de WebSocket.
     * @param message El mensaje a enviar.
     */
    override suspend fun sendMessage(message: Message) {
        val encryptedMessage = if (message.type == Message.MessageType.TEXT) {
            val encryptedContent = encryptionUtils.encrypt(message.content)
            message.copy(content = encryptedContent ?: message.content)
        } else {
            message
        }
        messageRemoteDataSource.sendMessage(encryptedMessage) // Persiste en Firestore.
        webSocketManager.sendMessage(encryptedMessage) // Envía por WebSocket para tiempo real.
        offlineDataSource.addMessage(encryptedMessage) // Añade a la caché local.
    }

    /**
     * Sube un archivo a Firebase Storage y luego envía un mensaje con su URL.
     * @param roomId El ID de la sala de chat.
     * @param senderId El ID del remitente.
     * @param fileUri La URI del archivo.
     * @param fileType El tipo de archivo.
     * @param fileName El nombre del archivo.
     * @return El [Message] enviado.
     */
    override suspend fun sendFile(
        roomId: String,
        senderId: String,
        fileUri: Uri,
        fileType: Message.MessageType,
        fileName: String
    ): Message {
        val fileUrl = messageRemoteDataSource.uploadFile(fileUri, fileType)
        val message = Message(
            roomId = roomId,
            senderId = senderId,
            content = "File: $fileName", // Contenido textual para el mensaje de archivo.
            timestamp = System.currentTimeMillis(),
            type = fileType,
            fileUrl = fileUrl,
            fileName = fileName,
            status = Message.MessageStatus.SENT
        )
        sendMessage(message) // Reutiliza la lógica de enviar mensaje.
        return message
    }

    /**
     * Actualiza el estado de un mensaje.
     * @param roomId El ID de la sala de chat.
     * @param messageId El ID del mensaje.
     * @param newStatus El nuevo estado.
     */
    override suspend fun updateMessageStatus(roomId: String, messageId: String, newStatus: Message.MessageStatus) {
        messageRemoteDataSource.updateMessageStatus(roomId, messageId, newStatus)
        offlineDataSource.updateMessageStatus(messageId, newStatus) // Actualiza también localmente.
    }
}
