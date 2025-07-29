package com.example.deflatam_chatapp.data.repository


import com.example.deflatam_chatapp.data.datasource.local.OfflineDataSource
import com.example.deflatam_chatapp.data.datasource.remote.ChatRoomRemoteDataSource
import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.example.deflatam_chatapp.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Implementación del repositorio de salas de chat.
 */
class ChatRoomRepositoryImpl @Inject constructor(
    private val chatRoomRemoteDataSource: ChatRoomRemoteDataSource,
    private val offlineDataSource: OfflineDataSource
) : ChatRoomRepository {

    /**
     * Obtiene un flujo de salas de chat, priorizando la fuente remota y actualizando la caché local.
     * @return [Flow] de [Result] con lista de [ChatRoom].
     */
    override fun getChatRooms(): Flow<Result<List<ChatRoom>>> {
        return chatRoomRemoteDataSource.getChatRooms().onEach { result ->
            result.onSuccess { chatRooms ->
                offlineDataSource.saveChatRooms(chatRooms) // Actualiza caché con datos remotos.
            }
        }
        // TODO: Implementar lógica de modo offline con sincronización. Por ahora, solo remoto.
        // Se podría combinar con offlineDataSource.getChatRooms() y aplicar una estrategia de sincronización.
    }

    /**
     * Crea una nueva sala de chat.
     * @param chatRoom La sala de chat a crear.
     * @return [Result] con el [ChatRoom] creado si tiene éxito.
     */
    override suspend fun createChatRoom(chatRoom: ChatRoom): Result<ChatRoom> {
        return chatRoomRemoteDataSource.createChatRoom(chatRoom)
    }

    /**
     * Se une a una sala de chat específica.
     * @param roomId El ID de la sala.
     * @param userId El ID del usuario.
     * @return [Result] de [Unit] si tiene éxito.
     */
    override suspend fun joinChatRoom(roomId: String, userId: String): Result<Unit> {
        return chatRoomRemoteDataSource.joinChatRoom(roomId, userId)
    }

    /**
     * Abandona una sala de chat específica.
     * @param roomId El ID de la sala.
     * @param userId El ID del usuario.
     * @return [Result] de [Unit] si tiene éxito.
     */
    override suspend fun leaveChatRoom(roomId: String, userId: String): Result<Unit> {
        return chatRoomRemoteDataSource.leaveChatRoom(roomId, userId)
    }
}
