package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.repository.MessageRepository


/**
 * Caso de uso para enviar un nuevo mensaje.
 */
class SendMessageUseCase(private val messageRepository: MessageRepository) {
    /**
     * Ejecuta la operación de envío de mensaje.
     * @param message El objeto [Message] a enviar.
     * @return Un [Result] que contiene el objeto [Message] enviado si es exitoso.
     */
    suspend operator fun invoke(message: Message): Result<Message> {
        return messageRepository.sendMessage(message)
    }
}
