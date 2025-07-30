package com.example.deflatam_chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.usecase.GetCurrentUserUseCase
import com.example.deflatam_chatapp.domain.usecase.LoginUserUseCase
import com.example.deflatam_chatapp.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel para la pantalla de inicio de sesión y registro de usuarios.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase // Inyecta el caso de uso para obtener el usuario actual
) : ViewModel() {

    /**
     * Clase sellada para representar los diferentes estados de autenticación.
     */
    sealed class AuthState {
        object Loading : AuthState()
        object Unauthenticated : AuthState()
        data class Authenticated(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Inicia sesión con las credenciales proporcionadas.
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.emit(AuthState.Loading).runCatching {
                val result = loginUserUseCase(email, password)
                val verify = result.username
                if (verify.isNotEmpty()) {
                    _authState.emit(AuthState.Authenticated(result))
                    checkCurrentUser()
                } else {
                    _authState.emit(AuthState.Error("Error al iniciar sesión"))
                }
            }.onFailure {
                _authState.emit(AuthState.Error(it.message ?: "Error desconocido al iniciar sesión."))
            }

            /*when (val result = loginUserUseCase(email, password)) {

                is ResultSession.Success -> {
                    // Tras un inicio de sesión exitoso, verificamos el usuario actual para obtener sus detalles.
                    checkCurrentUser()
                    Log.d("LoginViewModel", "Inicio de sesión exitoso. Usuario: ${result.data}")
                    _authState.emit(AuthState.Authenticated(result.data as User))
                }
                is ResultSession.Error -> {
                    _authState.emit(AuthState.Error(result.exception.message ?: "Error desconocido al iniciar sesión."))
                }
                is ResultSession.Loading -> {
                    // El estado de carga ya fue emitido al principio de la función.
                    Log.d("LoginViewModel", "Cargando...")
                    _authState.emit(AuthState.Loading)
                }
            }*/
        }
    }

    /**
     * Registra un nuevo usuario con las credenciales proporcionadas.
     * @param email El correo electrónico del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     * @param username El nombre de usuario del nuevo usuario.
     */
    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            _authState.emit(AuthState.Loading)
            val result = registerUserUseCase(email, password, username)
            val verify = result.username
            if (verify.isNotEmpty()) {
                _authState.emit(AuthState.Authenticated(result))
                checkCurrentUser()
            } else {
                _authState.emit(AuthState.Error("Error al registrar el usuario"))
            }

            /*when (val result = registerUserUseCase(email, password, username)) {
                is ResultSession.Success -> {
                    // Después de un registro exitoso, el usuario aún necesita iniciar sesión.
                    // Emitimos Unauthenticated para que la UI sepa que se ha completado el registro
                    // pero no se ha iniciado sesión automáticamente.
                    _authState.emit(AuthState.Unauthenticated)
                    // Podrías añadir lógica para iniciar sesión automáticamente si lo deseas: checkCurrentUser()
                }
                is ResultSession.Error -> {
                    _authState.emit(AuthState.Error(result.exception.message ?: "Error desconocido al registrar."))
                }
                is ResultSession.Loading -> {
                    // El estado de carga ya fue emitido al principio de la función.
                }
            }*/
        }
    }

    /**
     * Verifica si ya hay un usuario autenticado al iniciar la actividad.
     */
    fun checkCurrentUser() {
        viewModelScope.launch {
            _authState.emit(AuthState.Loading)
            getCurrentUserUseCase().collect { result ->
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _authState.emit(AuthState.Authenticated(user))
                    } else {
                        _authState.emit(AuthState.Unauthenticated)
                    }

                }
                /*when (result) {
                    is ResultSession.Success -> {
                        val user = result.data
                        if (user != null) {
                            _authState.emit(AuthState.Authenticated(user as User))
                        } else {
                            _authState.emit(AuthState.Unauthenticated)
                        }
                    }
                    is ResultSession.Error -> {
                        // Si hay un error al verificar el usuario, asumimos que no está autenticado.
                        _authState.emit(AuthState.Unauthenticated)
                        Log.e("LoginViewModel", "Error al verificar usuario actual: ${result.exception.message}")
                    }
                    is ResultSession.Loading -> {
                        // No es necesario emitir aquí, ya se emitió al principio de checkCurrentUser.
                    }
                }*/
            }
        }
    }
}