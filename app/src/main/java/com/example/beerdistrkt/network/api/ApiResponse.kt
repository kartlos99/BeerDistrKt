package com.example.beerdistrkt.network.api

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