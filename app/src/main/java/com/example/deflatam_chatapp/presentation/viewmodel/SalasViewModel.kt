package com.example.deflatam_chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.example.deflatam_chatapp.domain.usecase.CreateChatRoomUseCase
import com.example.deflatam_chatapp.domain.usecase.GetChatRoomsUseCase
import com.example.deflatam_chatapp.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


/**
 * ViewModel para la pantalla de salas de chat.
 */
@HiltViewModel
class SalasViewModel @Inject constructor(
    private val getChatRoomsUseCase: GetChatRoomsUseCase,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    private var currentUser = "Usuario?"

    /**
     * Eventos de la interfaz de usuario para la pantalla de salas.
     */
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent() // Muestra un mensaje Toast.
        object RoomCreated : UiEvent() // Evento cuando una sala es creada exitosamente.
    }

    init {
        // Inicia la observación de las salas de chat al inicializar el ViewModel.
        getChatRooms()
        getCurrentUser()
    }

    /**
     * Obtiene el usuario actual autenticado.
     */
    fun getCurrentUser(){
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                if (result.isSuccess) {
                    currentUser = result.getOrNull()!!.username
                }
                else {
                    val errorMessage = result.exceptionOrNull()?.message
                }
            }
        }
    }

    /**
     * Obtiene y observa la lista de salas de chat.
     */
    private fun getChatRooms() {
        viewModelScope.launch {
            getChatRoomsUseCase().collect { rooms ->
                _chatRooms.value = rooms
            }
        }
    }

    /**
     * Crea una nueva sala de chat.
     * @param roomName Nombre de la sala a crear.
     */
    fun createChatRoom(roomName: String) {
        val userName = currentUser
        viewModelScope.launch {
            try {
                val newRoom = ChatRoom(
                    id = UUID.randomUUID().toString(),
                    name = roomName,
                    description = "Sala creada por el usuario: $userName",
                    participants = emptyList(), // Se añadirán participantes al unirse.
                    createdAt = System.currentTimeMillis()
                )
                createChatRoomUseCase(newRoom)
                _eventFlow.emit(UiEvent.RoomCreated)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Error al crear la sala: ${e.localizedMessage}"))
            }
        }
    }
}
