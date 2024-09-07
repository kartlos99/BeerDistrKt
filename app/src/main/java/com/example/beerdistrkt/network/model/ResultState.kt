package com.example.beerdistrkt.network.model

sealed class ResultState<out T> {
    data object Loading : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(
        val statusCode: Int = 0,
        val message: String? = null,
        val errorCode: String? = null
    ) : ResultState<Nothing>()
}


fun <T> T.asSuccessState(): ResultState.Success<T> {
    return ResultState.Success(this)
}

fun <T> ResultState<T>.onSuccess(onSuccess: (data: T) -> Unit) {
    if (this is ResultState.Success)
        onSuccess(this.data)
}

fun <T> ResultState<T>.onError(onError: (code: String?, message: String?) -> Unit) {
    if (this is ResultState.Error)
        onError(this.errorCode, this.message)
}