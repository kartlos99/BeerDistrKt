package com.example.beerdistrkt

import android.content.Context
import android.net.ConnectivityManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

//<Response : Any, ApiResponse : DataResponse<Response>>
fun <T: Any>  Call<T>.sendRequest(
    success: ((data: T) -> Unit),
    failure: ((t: Throwable) -> Unit),
    onConnectionFailure: (Throwable) -> Unit,
    finally: ((success: Boolean) -> Unit)? = null
){
    enqueue(object :Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            finally?.invoke(false)
            if (t is IOException) {
                onConnectionFailure(t)
            } else {
                failure.invoke(t)
            }
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            finally?.invoke(true)
            if (response.isSuccessful){
                val body = response.body()
                body?.let {
                    success(it)
                }
            }
        }
    })
}


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}