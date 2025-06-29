package com.example.beerdistrkt.network.model

sealed class ResultState<out T> {
    data object Loading : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(
        val statusCode: Int = 0,
        val message: String? = null,
        val errorCode: String? = null
    ) : ResultState<Nothing>() {

        val formatedMessage: String
            get() = "Error: code=$errorCode - msg=$message"
    }
}


fun <T> T.asSuccessState(): ResultState.Success<T> {
    return ResultState.Success(this)
}

fun <T> ResultState<T>.onSuccess(onSuccess: (data: T) -> Unit): ResultState<T> {
    if (this is ResultState.Success)
        onSuccess(this.data)
    return this
}

fun <T> ResultState<T>.onError(onError: (code: String?, message: String?) -> Unit): ResultState<T> {
    if (this is ResultState.Error)
        onError(this.errorCode, this.message)
    return this
}

fun <T> ResultState<T>.onError(onError: (error: ResultState.Error) -> Unit): ResultState<T> {
    if (this is ResultState.Error)
        onError(this)
    return this
}

fun <T> ResultState<T>.onLoading(onLoading: () -> Unit): ResultState<T> {
    if (this is ResultState.Loading)
        onLoading()
    return this
}

fun <T> ResultState<T>.isLoading() = this == ResultState.Loading

fun <T> ResultState<T>.isError() = this is ResultState.Error

fun <T> ResultState<T>.isSuccess() = this is ResultState.Success<T>

fun <T, R> ResultState<T>.transform(convert: (data: T) -> R): ResultState<R> {
    return when (this) {
        ResultState.Loading -> ResultState.Loading
        is ResultState.Error -> this
        is ResultState.Success -> ResultState.Success(convert(this.data))
    }
}