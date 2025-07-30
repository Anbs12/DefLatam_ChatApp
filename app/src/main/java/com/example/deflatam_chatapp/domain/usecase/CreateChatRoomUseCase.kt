package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.example.deflatam_chatapp.domain.repository.ChatRoomRepository
import javax.inject.Inject

/**
 * Caso de uso para crear una nueva sala de chat.
 */
class CreateChatRoomUseCase @Inject constructor(private val chatRoomRepository: ChatRoomRepository) {
    /**
     * Ejecuta el caso de uso para crear una sala de chat.
     * @param chatRoom La sala de chat a crear.
     */
    suspend operator fun invoke(chatRoom: ChatRoom) {
        chatRoomRepository.createChatRoom(chatRoom)
    }
}