package com.example.deflatam_chatapp.data.datasource.remote

import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 * Fuente de datos remota para las operaciones de salas de chat.
 */
class ChatRoomRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val chatRoomsCollection = firestore.collection("chatRooms")

    /**
     * Obtiene un flujo de todas las salas de chat disponibles.
     * @return Un [Flow] que emite una lista de [ChatRoom].
     */
    fun getChatRooms(): Flow<Result<List<ChatRoom>>> = callbackFlow {
        val subscription = chatRoomsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(Result.failure(e)).isSuccess
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val chatRooms = snapshot.documents.mapNotNull { it.toObject<ChatRoom>() }
                trySend(Result.success(chatRooms)).isSuccess
            } else {
                trySend(Result.success(emptyList())).isSuccess // No hay snapshot, retornar lista vacía.
            }
        }
        awaitClose { subscription.remove() } // Limpiar el listener al cerrar el flujo.
    }

    /**
     * Crea una nueva sala de chat.
     * @param chatRoom La sala de chat a crear.
     * @return El objeto [ChatRoom] creado si la operación es exitosa.
     */
    suspend fun createChatRoom(chatRoom: ChatRoom): Result<ChatRoom> = runCatching {
        val newDocRef = chatRoomsCollection.document(chatRoom.id) // Usar el ID provisto por el modelo
        newDocRef.set(chatRoom).await()
        chatRoom
    }

    /**
     * Se une a una sala de chat específica.
     * @param roomId El ID de la sala a la que unirse.
     * @param userId El ID del usuario que se une.
     * @return Un [Result] indicando el éxito o fallo de la operación.
     */
    suspend fun joinChatRoom(roomId: String, userId: String): Result<Unit> = runCatching {
        val roomRef = chatRoomsCollection.document(roomId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(roomRef)
            val currentParticipants = snapshot.get("participants") as? List<String> ?: emptyList()
            if (!currentParticipants.contains(userId)) {
                transaction.update(roomRef, "participants", currentParticipants + userId)
            }
            // No need to return anything for Unit.
        }.await()
    }

    /**
     * Abandona una sala de chat específica.
     * @param roomId El ID de la sala a abandonar.
     * @param userId El ID del usuario que abandona.
     * @return Un [Result] indicando el éxito o fallo de la operación.
     */
    suspend fun leaveChatRoom(roomId: String, userId: String): Result<Unit> = runCatching {
        val roomRef = chatRoomsCollection.document(roomId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(roomRef)
            val currentParticipants = snapshot.get("participants") as? List<String> ?: emptyList()
            if (currentParticipants.contains(userId)) {
                transaction.update(roomRef, "participants", currentParticipants - userId)
            }
            // No need to return anything for Unit.
        }.await()
    }
}
