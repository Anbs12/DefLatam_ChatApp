package com.example.deflatam_chatapp.data.repository


import com.example.deflatam_chatapp.data.datasource.local.OfflineDataSource
import com.example.deflatam_chatapp.data.datasource.remote.ChatRoomRemoteDataSource
import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.example.deflatam_chatapp.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta de [ChatRoomRepository] que gestiona las salas de chat.
 */
@Singleton
class ChatRoomRepositoryImpl @Inject constructor(
    private val chatRoomRemoteDataSource: ChatRoomRemoteDataSource,
    private val offlineDataSource: OfflineDataSource
) : ChatRoomRepository {

    /**
     * Obtiene un flujo de salas de chat. Prioriza la fuente remota y actualiza el caché local.
     * @return Un [Flow] que emite una lista de [ChatRoom].
     */
    override fun getChatRooms(): Flow<List<ChatRoom>> {
        return chatRoomRemoteDataSource.getChatRooms().onEach { remoteChatRooms ->
            offlineDataSource.saveChatRooms(remoteChatRooms) // Sincroniza con Room
        }
        // En una app más robusta, se combinaría con offlineDataSource.getChatRooms()
        // para mostrar datos cacheados mientras se cargan los remotos.
    }

    /**
     * Crea una nueva sala de chat.
     * @param chatRoom La sala de chat a crear.
     */
    override suspend fun createChatRoom(chatRoom: ChatRoom) {
        chatRoomRemoteDataSource.createChatRoom(chatRoom)
        offlineDataSource.saveChatRooms(listOf(chatRoom)) // Guarda también localmente
    }
}
