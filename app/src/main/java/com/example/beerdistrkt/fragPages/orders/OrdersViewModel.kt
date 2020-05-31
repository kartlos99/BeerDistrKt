package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService

class OrdersViewModel : BaseViewModel() {
    // TODO: Implement the ViewModel
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    val ordersLiveData = MutableLiveData<List<Order>>()

    init {
        getOrders()
    }


    private fun getOrders() {
        sendRequest(
            ApeniApiService.getInstance().getOrders("2020-04-14"),
            successWithData = {
                ordersLiveData.value = it
            },
            failure = {
                Log.d("getOrder", "failed: ${it.message}")
            }
        )
    }


    companion object {
        const val TAG = "ordersVM"
    }
}
