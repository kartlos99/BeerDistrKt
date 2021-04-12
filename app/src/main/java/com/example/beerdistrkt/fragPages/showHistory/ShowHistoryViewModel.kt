package com.example.beerdistrkt.fragPages.showHistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState

class ShowHistoryViewModel : BaseViewModel() {
    private val TAG = "ShowHistoryViewModel"

    private val beerLiveData = database.getBeerList()
    lateinit var beerMap : Map<Int, BeerModel>

    private val _orderHistoryLiveData = MutableLiveData<ApiResponseState<List<OrderHistoryDTO>>>()
    val orderHistoryLiveData: LiveData<ApiResponseState<List<OrderHistoryDTO>>>
        get() = _orderHistoryLiveData

    init {
        beerLiveData.observeForever { beerList ->
            beerMap = beerList.groupBy { it.id }.mapValues {
                it.value[0]
            }
        }
    }

    fun getData(recordID: String) {
        sendRequest(
            ApeniApiService.getInstance().getOrderHistory(recordID),
            successWithData = {
                _orderHistoryLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                Log.d(TAG, "getData: finaly = $it")
            }
        )
    }

}