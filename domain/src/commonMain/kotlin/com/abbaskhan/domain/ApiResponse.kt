package com.abbaskhan.domain

sealed interface ApiResponse<out T> {

    data object Loading : ApiResponse<Nothing>

    data class Success<T>(
        val data: T
    ) : ApiResponse<T>

    data class Error(
        val message: AppError
    ) : ApiResponse<Nothing>

}

sealed interface AppError {
    data object Network : AppError
    data object NotFound : AppError
    data object Unknown : AppError
}