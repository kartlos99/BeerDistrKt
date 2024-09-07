package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.network.model.ResultState

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(
        val statusCode: Int = 0,
        val message: String? = null,
        val errorCode: String? = null
    ) : ApiResponse<Nothing>()
}

fun <T> T.asSuccessResponse(): ApiResponse.Success<T> {
    return ApiResponse.Success(this)
}

fun <T> ApiResponse<T>.toResultState(): ResultState<T> = when (this) {

    is ApiResponse.Error -> ResultState.Error(
        statusCode = statusCode,
        message = message,
        errorCode = errorCode
    )

    is ApiResponse.Success -> ResultState.Success(data)
}

const val DUPLICATE_ENTRY_API_ERROR_CODE = "1062"