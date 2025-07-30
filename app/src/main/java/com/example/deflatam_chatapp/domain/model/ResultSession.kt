package com.example.deflatam_chatapp.domain.model

/**
 * Clase sellada para representar el resultado de una operación.
 */
sealed class ResultSession {
    /**
     * Indica que la operación está en curso.
     */
    object Loading : ResultSession()

    /**
     * Indica que la operación se completó con éxito.
     * @param data Los datos resultantes de la operación.
     */
    data class Success(val data: User) : ResultSession()

    /**
     * Indica que la operación falló.
     * @param exception La excepción que causó el fallo.
     */
    data class Error(val exception: Throwable) : ResultSession()
}