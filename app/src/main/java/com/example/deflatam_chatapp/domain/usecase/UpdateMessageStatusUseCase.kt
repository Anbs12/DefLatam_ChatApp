package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.repository.MessageRepository
import javax.inject.Inject

/**
 * Caso de uso para actualizar el estado de un mensaje (enviado, entregado, leído).
 */
class UpdateMessageStatusUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    /**
     * Ejecuta el caso de uso para actualizar el estado de un mensaje.
     * @param messageId El ID del mensaje a actualizar.
     * @param newStatus El nuevo estado del mensaje.
     * @return Un Flow que emite el Result de la operación.
     */
    suspend operator fun invoke(
        roomId: String,
        messageId: String,
        newStatus: Message.MessageStatus
    ) {
        messageRepository.updateMessageStatus(roomId, messageId, newStatus)
    }

}