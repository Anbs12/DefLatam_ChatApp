package com.example.deflatam_chatapp.domain.usecase

import android.net.Uri
import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.repository.MessageRepository
import javax.inject.Inject

/**
 * Caso de uso para enviar un archivo (imagen o documento) en un chat.
 */
class SendFileUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    /**
     * Ejecuta el caso de uso para enviar un archivo.
     * @param roomId El ID de la sala de chat.
     * @param senderId El ID del remitente del archivo.
     * @param fileUri La URI del archivo a enviar.
     * @param fileName El nombre del archivo.
     * @param fileType El tipo de archivo (e.g., "image/jpeg", "application/pdf").
     * @return Un Flow que emite el Result de la operación.
     */
    suspend operator fun invoke(
        roomId: String,
        senderId: String,
        fileUri: Uri,
        fileName: String,
        fileType: Message.MessageType
    ): Message {
        return messageRepository.sendFile(
            roomId = roomId,
            senderId = senderId,
            fileUri = fileUri,
            fileName = fileName,
            fileType = fileType
        )
    }
}