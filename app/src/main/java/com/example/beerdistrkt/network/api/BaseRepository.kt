package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.ifNullOrEmpty
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository(private val ioDispatcher: CoroutineDispatcher) {

    suspend fun <T> apiCall(apiCall: suspend () -> T): ApiResponse<T> {
        return withContext(ioDispatcher) {
            try {
                ApiResponse.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ApiResponse.Error(IO_EXCEPTION_CODE, "ioException")
                    is HttpException -> with(convertErrorBody(throwable)) {
                        this?.let { errorResponse ->
                            ApiResponse.Error(
                                throwable.code(),
                                errorResponse.errorMessage.ifNullOrEmpty(throwable.message()),
                                errorResponse.errorCode
                            )
                        } ?: ApiResponse.Error(PARSE_EXCEPTION_CODE, throwable.message)
                    }

                    else -> ApiResponse.Error(PARSE_EXCEPTION_CODE, throwable.message)
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.string()?.let {
                val moshiAdapter = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build().adapter(ErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            null
        }
    }

    companion object {
        const val IO_EXCEPTION_CODE = -1
        const val PARSE_EXCEPTION_CODE = -2
    }
}


data class ErrorResponse(
    val errorMessage: String? = null,
    val errorCode: String? = null,
)