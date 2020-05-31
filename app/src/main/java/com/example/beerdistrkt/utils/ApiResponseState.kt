package com.example.beerdistrkt.utils

import androidx.annotation.StringRes

sealed class ApiResponseState<out T> {
    data class Loading(val showLoading: Boolean, val isSuccess: Boolean = true) : ApiResponseState<Nothing>()
    data class Success<T>(val data: T) : ApiResponseState<T>()
    data class ApiError(val errorCode: Int, val errorText: String) : ApiResponseState<Nothing>()
    data class Error<T>(val data: T) : ApiResponseState<T>()
    data class ErrorRes<T>(@StringRes val stringRes: Int) : ApiResponseState<T>()
    object EmptyResult : ApiResponseState<Nothing>()
    object Sleep : ApiResponseState<Nothing>()
    object NoInternetConnection : ApiResponseState<Nothing>()
}

fun <T> T.asSuccessState(): ApiResponseState.Success<T> {
    return ApiResponseState.Success(this)
}