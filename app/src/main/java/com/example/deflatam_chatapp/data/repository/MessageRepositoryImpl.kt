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
import kotlinx.coroutines.flow.onStart
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
    override fun getMessages(roomId: String): Flow<List<Message>> =
        combine(
            // 1) Firestore
            messageRemoteDataSource.getMessages(roomId),

            // 2) WebSocket, emitiendo [] al arrancar
            webSocketManager.incomingMessages
                .map { msg -> if (msg.roomId == roomId) listOf(msg) else emptyList() }
                .distinctUntilChanged()
                .onEach { incoming -> if (incoming.isNotEmpty()) offlineDataSource.saveMessages(incoming) }
                .onStart { emit(emptyList()) },

            // 3) Room, emitiendo [] al arrancar
            offlineDataSource.getMessages(roomId)
                .distinctUntilChanged()
                .onStart { emit(emptyList()) }

        ) { remote, websocket, local ->
            // Mismo merge + decrypt…
            val combined = (remote + websocket + local)
                .distinctBy { it.id }
                .sortedBy { it.timestamp }
            combined.map { m ->
                if (m.type == Message.MessageType.TEXT)
                    m.copy(content = encryptionUtils.decrypt(m.content) ?: m.content)
                else m
            }
        }
            .distinctUntilChanged()


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
