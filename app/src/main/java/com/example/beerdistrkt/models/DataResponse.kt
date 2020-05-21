package com.example.beerdistrkt.models

data class DataResponse<T: Any>(
    val success: Boolean,
    val errorText: String?,
    val errorCode: Int?,
    val data: T?
) {
}