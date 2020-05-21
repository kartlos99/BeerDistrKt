package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import retrofit2.Call
import retrofit2.Response

class OrdersViewModel : BaseViewModel() {
    // TODO: Implement the ViewModel
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    init {
        getOrders()
    }

    fun btn1click(){
        getData()
    }

    private fun getOrders() {
        sendRequest(
            ApeniApiService.getInstance().getOrders("2020-04-14"),
            success = {
            },
            failure = {
            }
        )
    }

    private fun getData() {
        _response.value = "GetDaTAAAAA"
        ApeniApiService.getInstance().getUsersList().enqueue(object : retrofit2.Callback<List<User>>{
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
