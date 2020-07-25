package com.example.beerdistrkt.models

data class DataResponse<T: Any>(
    val success: Boolean,
    val errorText: String?,
    val errorCode: Int?,
    val data: T?
) {

    companion object {

        @JvmField
        val ErrorCodeDataIsNull = 420

        @JvmField
        val UnknownError = 999

        @JvmField
        val mySqlDuplicateError = 1062
    }
}