package com.example.deflatam_chatapp.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.usecase.GetCurrentUserUseCase
import com.example.deflatam_chatapp.domain.usecase.GetMessagesUseCase
import com.example.deflatam_chatapp.domain.usecase.SendFileUseCase
import com.example.deflatam_chatapp.domain.usecase.SendMessageUseCase
import com.example.deflatam_chatapp.domain.usecase.UpdateMessageStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de chat de una sala específica.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val sendFileUseCase: SendFileUseCase,
    private val updateMessageStatusUseCase: UpdateMessageStatusUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    // ID del usuario actual. Necesario para determinar si un mensaje es propio o recibido.
    var currentUserId: String = ""
        private set

    private var currentRoomId: String? = null

    /**
     * Eventos de la interfaz de usuario para la pantalla de chat.
     */
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent() // Muestra un mensaje Toast.
        object MessageSent : UiEvent() // Evento cuando un mensaje es enviado exitosamente.
    }

    init {
        // Obtener el ID del usuario actual al inicializar el ViewModel.
        viewModelScope.launch {
            getCurrentUserUseCase()?.let {
                currentUserId = it.collect { result ->
                    result.getOrNull()
                }.toString()
            } ?: run {
                _eventFlow.emit(UiEvent.ShowToast("Usuario no autenticado."))
            }
        }
    }

    /**
     * Comienza a observar los mensajes para una sala de chat específica.
     * @param roomId El ID de la sala de chat.
     */
    fun startObservingMessages(roomId: String) {
        currentRoomId = roomId
        viewModelScope.launch {
            getMessagesUseCase(roomId).collect { messages ->
                _messages.value = messages
            }
        }
    }

    /**
     * Envía un nuevo mensaje.
     * @param content Contenido del mensaje.
     * @param type Tipo de mensaje (TEXT, IMAGE, FILE).
     * @param fileUrl URL del archivo (solo si es tipo IMAGE o FILE).
     * @param fileName Nombre del archivo (solo si es tipo IMAGE o FILE).
     */
    fun sendMessage(
        content: String,
        type: Message.MessageType = Message.MessageType.TEXT,
        fileUrl: String? = null,
        fileName: String? = null
    ) {
        viewModelScope.launch {
            if (currentRoomId == null || currentUserId.isEmpty()) {
                _eventFlow.emit(UiEvent.ShowToast("Error: ID de sala o usuario no disponible."))
                return@launch
            }

            val message = Message(
                roomId = currentRoomId!!,
                senderId = currentUserId,
                content = content,
                timestamp = System.currentTimeMillis(),
                type = type,
                fileUrl = fileUrl,
                fileName = fileName,
                status = Message.MessageStatus.SENT // Estado inicial al enviar.
            )

            try {
                sendMessageUseCase(message)
                _eventFlow.emit(UiEvent.MessageSent)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Error al enviar mensaje: ${e.localizedMessage}"))
                Log.e("ChatViewModel", "Error al enviar mensaje", e)
            }
        }
    }

    /**
     * Envía un archivo (imagen o documento).
     * @param fileUri La URI del archivo a subir.
     * @param fileName El nombre del archivo.
     */
    fun sendFile(fileUri: Uri, fileName: String) {
        viewModelScope.launch {
            if (currentRoomId == null || currentUserId.isEmpty()) {
                _eventFlow.emit(UiEvent.ShowToast("Error: ID de sala o usuario no disponible para enviar archivo."))
                return@launch
            }

            val fileType = if (fileName.contains("image", ignoreCase = true) || fileName.endsWith(
                    ".jpg",
                    true
                ) || fileName.endsWith(".png", true) || fileName.endsWith(".jpeg", true)
            ) {
                Message.MessageType.IMAGE
            } else {
                Message.MessageType.FILE
            }

            try {
                _eventFlow.emit(UiEvent.ShowToast("Subiendo archivo..."))
                sendFileUseCase(currentRoomId!!, currentUserId, fileUri, fileName, fileType)
                _eventFlow.emit(UiEvent.MessageSent)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Error al enviar archivo: ${e.localizedMessage}"))
            }
        }
    }

    /**
     * Actualiza el estado de un mensaje.
     * @param messageId ID del mensaje a actualizar.
     * @param newStatus Nuevo estado del mensaje.
     */
    fun updateMessageStatus(messageId: String, newStatus: Message.MessageStatus) {
        viewModelScope.launch {
            currentRoomId?.let { roomId ->
                try {
                    updateMessageStatusUseCase(roomId, messageId, newStatus)
                } catch (e: Exception) {
                    _eventFlow.emit(UiEvent.ShowToast("Error al actualizar estado del mensaje: ${e.localizedMessage}"))
                }
            }
        }
    }
}
