package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.example.deflatam_chatapp.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener un flujo de todas las salas de chat disponibles.
 *
 * @param chatRoomRepository Repositorio para acceder a los datos de las salas de chat.
 */
class GetChatRoomsUseCase @Inject constructor(
    private val chatRoomRepository: ChatRoomRepository
) {
    /**
     * Invoca el caso de uso para obtener las salas de chat.
     * @return Un [Flow] que emite una lista de [ChatRoom].
     */
    operator fun invoke(): Flow<List<ChatRoom>> {
        return chatRoomRepository.getChatRooms()
    }
}