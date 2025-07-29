package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.User
import com.example.deflatam_chatapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Caso de uso para obtener el usuario actualmente autenticado.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Ejecuta el caso de uso para obtener el usuario actual.
     * @return Un Flow que emite el Result de la operación, conteniendo el User si está autenticado, o null.
     */
    suspend operator fun invoke(): Flow<Result<User?>> {
        val data = authRepository.getCurrentUser()
        return flowOf(Result.success(data))
    }
}