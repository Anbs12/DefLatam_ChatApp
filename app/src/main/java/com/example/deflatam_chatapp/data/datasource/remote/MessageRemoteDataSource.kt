package com.example.deflatam_chatapp.data.datasource.remote

import android.net.Uri
import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Fuente de datos remota para mensajes utilizando Firebase Firestore y Storage.
 */
@Singleton
class MessageRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) {

    /**
     * Obtiene un flujo de mensajes para una sala de chat específica desde Firestore.
     * @param roomId El ID de la sala de chat.
     * @return Un [Flow] que emite una lista de [Message].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMessages(roomId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection(Constants.FIRESTORE_COLLECTION_CHATROOMS)
            .document(roomId)
            .collection(Constants.FIRESTORE_COLLECTION_MESSAGES)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // Cierra el flujo con el error.
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                    trySend(messages).isSuccess // Emite la lista de mensajes.
                } else {
                    trySend(emptyList()).isSuccess // Emite una lista vacía si no hay snapshot.
                }
            }
        awaitClose { subscription.remove() } // Cancela el listener cuando el flujo deja de ser recogido.
    }

    /**
     * Envía un nuevo mensaje a Firestore.
     * @param message El mensaje a enviar.
     * @throws Exception Si el envío del mensaje falla.
     */
    suspend fun sendMessage(message: Message) {
        firestore.collection(Constants.FIRESTORE_COLLECTION_CHATROOMS)
            .document(message.roomId)
            .collection(Constants.FIRESTORE_COLLECTION_MESSAGES)
            .document(message.id)
            .set(message)
            .await()
    }

    /**
     * Sube un archivo (imagen o documento) a Firebase Storage.
     * @param fileUri La URI del archivo a subir.
     * @param fileType El tipo de archivo (IMAGE o FILE).
     * @return La URL de descarga del archivo subido.
     * @throws Exception Si la subida del archivo falla.
     */
    suspend fun uploadFile(fileUri: Uri, fileType: Message.MessageType): String {
        val storageRef = firebaseStorage.reference
        val path = when (fileType) {
            Message.MessageType.IMAGE -> Constants.FIREBASE_STORAGE_IMAGES_PATH
            Message.MessageType.FILE -> Constants.FIREBASE_STORAGE_FILES_PATH
            else -> throw IllegalArgumentException("Unsupported file type for upload: $fileType")
        }
        val fileName = UUID.randomUUID().toString() + "_" + fileUri.lastPathSegment
        val fileRef = storageRef.child("$path$fileName")

        val uploadTask = fileRef.putFile(fileUri).await()
        return fileRef.downloadUrl.await().toString()
    }

    /**
     * Actualiza el estado de un mensaje en Firestore.
     * @param roomId El ID de la sala de chat.
     * @param messageId El ID del mensaje a actualizar.
     * @param newStatus El nuevo estado del mensaje.
     * @throws Exception Si la actualización del estado falla.
     */
    suspend fun updateMessageStatus(roomId: String, messageId: String, newStatus: Message.MessageStatus) {
        firestore.collection(Constants.FIRESTORE_COLLECTION_CHATROOMS)
            .document(roomId)
            .collection(Constants.FIRESTORE_COLLECTION_MESSAGES)
            .document(messageId)
            .update("status", newStatus.name)
            .await()
    }
}
