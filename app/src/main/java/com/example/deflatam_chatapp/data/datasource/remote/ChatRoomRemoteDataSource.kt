package com.example.deflatam_chatapp.data.datasource.remote

import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Fuente de datos remota para salas de chat utilizando Firebase Firestore.
 */
@Singleton
class ChatRoomRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /**
     * Obtiene un flujo de todas las salas de chat desde Firestore.
     * @return Un [Flow] que emite una lista de [ChatRoom].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getChatRooms(): Flow<List<ChatRoom>> = callbackFlow {
        val subscription = firestore.collection("chatRooms")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // Cierra el flujo con el error.
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val chatRooms = snapshot.documents.mapNotNull { it.toObject(ChatRoom::class.java) }
                    trySend(chatRooms).isSuccess // Emite la lista de salas de chat.
                } else {
                    trySend(emptyList()).isSuccess // Emite una lista vacía si no hay snapshot.
                }
            }
        awaitClose { subscription.remove() } // Cancela el listener cuando el flujo deja de ser recogido.
    }

    /**
     * Crea una nueva sala de chat en Firestore.
     * @param chatRoom La sala de chat a crear.
     * @throws Exception Si la creación de la sala falla.
     */
    suspend fun createChatRoom(chatRoom: ChatRoom) {
        firestore.collection("chatRooms").document(chatRoom.id).set(chatRoom).await()
    }
}
