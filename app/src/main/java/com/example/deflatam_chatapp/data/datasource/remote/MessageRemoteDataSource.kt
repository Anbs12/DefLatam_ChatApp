package com.example.deflatam_chatapp.data.datasource.remote

import android.net.Uri
import com.example.deflatam_chatapp.domain.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


/**
 * Fuente de datos remota para las operaciones de mensajes.
 */
class MessageRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) {
    private val messagesCollection = firestore.collection("messages")
    private val storageRef = firebaseStorage.reference

    /**
     * Envía un nuevo mensaje a Firestore.
     * @param message El objeto [Message] a enviar.
     * @return El objeto [Message] enviado si la operación es exitosa.
     */
    suspend fun sendMessage(message: Message): Result<Message> = runCatching {
        val docRef = messagesCollection.document(message.id)
        docRef.set(message).await()
        message
    }

    /**
     * Obtiene un flujo de mensajes para una sala de chat específica.
     * Los mensajes se ordenan por marca de tiempo.
     * @param roomId El ID de la sala de chat.
     * @return Un [Flow] que emite una lista de [Message] para la sala.
     */
    fun getMessages(roomId: String): Flow<Result<List<Message>>> = callbackFlow {
        val query = messagesCollection
            .whereEqualTo("roomId", roomId)
            .orderBy("timestamp", Query.Direction.ASCENDING) // Ordenar por timestamp

        val subscription = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(Result.failure(e)).isSuccess
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val messages = snapshot.documents.mapNotNull { it.toObject<Message>() }
                trySend(Result.success(messages)).isSuccess
            } else {
                trySend(Result.success(emptyList())).isSuccess
            }
        }
        awaitClose { subscription.remove() } // Limpiar el listener al cerrar el flujo.
    }

    /**
     * Actualiza el estado de un mensaje en Firestore.
     * @param messageId El ID del mensaje a actualizar.
     * @param status El nuevo estado del mensaje (ej. "DELIVERED", "READ").
     * @return Un [Result] indicando el éxito o fallo de la operación.
     */
    suspend fun updateMessageStatus(messageId: String, status: String): Result<Unit> = runCatching {
        messagesCollection.document(messageId)
            .update("status", status)
            .await()
    }

    /**
     * Sube un archivo a Firebase Storage y retorna su URL de descarga.
     * @param fileUri URI local del archivo a subir.
     * @param fileName Nombre del archivo.
     * @param messageType Tipo de mensaje (IMAGE o FILE) para determinar la ruta de almacenamiento.
     * @return La URL de descarga del archivo si la subida es exitosa.
     */
    suspend fun uploadFile(fileUri: Uri, fileName: String, messageType: Message.MessageType): Result<String> = runCatching {
        val path = when (messageType) {
            Message.MessageType.IMAGE -> "chat_images/"
            Message.MessageType.FILE -> "chat_files/"
            else -> throw IllegalArgumentException("Unsupported message type for file upload")
        }
        val fileExtension = fileName.substringAfterLast('.', "")
        val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"
        val fileRef = storageRef.child(path + uniqueFileName)

        fileRef.putFile(fileUri).await()
        fileRef.downloadUrl.await().toString()
    }
}
