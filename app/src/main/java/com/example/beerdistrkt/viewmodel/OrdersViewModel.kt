package com.example.beerdistrkt.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.network.ApeniApiService
import retrofit2.Call
import retrofit2.Response

class OrdersViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    init {
        Log.d(TAG, "init")

    }

    fun btn1click(){
        getData()
    }

    private fun getData() {
        _response.value = "GetDaTAAAAA"
        ApeniApiService.get().getProperties().enqueue(object : retrofit2.Callback<List<User>>{
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d(TAG, "fail")
                _response.value = "Fail ${t.message}"
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _response.value = "raodenoba ${response.body()?.size} obj"
                response.body()?.let {
                    if (it.isNotEmpty()){
                        _response.value = it.firstOrNull().toString()
                    }

                }
            }

        })

    }

    companion object {
        const val TAG = "ordersVM"
    }
}
