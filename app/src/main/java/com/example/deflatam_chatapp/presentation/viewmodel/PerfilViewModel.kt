package com.example.deflatam_chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.usecase.GetCurrentUserUseCase
import com.example.deflatam_chatapp.domain.usecase.LogoutUserUseCase
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
 * ViewModel para la pantalla de perfil del usuario.
 */
@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    /**
     * Eventos de la interfaz de usuario para la pantalla de perfil.
     */
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent() // Muestra un mensaje Toast.
        object LoggedOut : UiEvent() // Evento cuando el usuario cierra sesión.
    }

    init {
        // Carga el usuario actual al inicializar el ViewModel.
        loadCurrentUser()
    }

    /**
     * Carga los datos del usuario actualmente autenticado.
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                var user: User
                getCurrentUserUseCase().collect { result ->
                    val id = result.getOrNull()?.id!!
                    val nombre = result.getOrNull()?.username!!
                    val email = result.getOrNull()?.email!!
                    user = User(id, nombre, email)
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Error al cargar perfil: ${e.localizedMessage}"))
                _currentUser.value = null
            }
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                logoutUserUseCase()
                _eventFlow.emit(UiEvent.LoggedOut)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Error al cerrar sesión: ${e.localizedMessage}"))
            }
        }
    }
}
