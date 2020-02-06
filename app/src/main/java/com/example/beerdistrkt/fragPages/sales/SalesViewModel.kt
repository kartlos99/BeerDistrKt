package com.example.beerdistrkt.fragPages.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.SaleInfo
import com.example.beerdistrkt.network.ApeniApiService

class SalesViewModel : BaseViewModel() {

    val TAG = "Sales_VM"
    var k30empty = 0.0f
    var k50empty = 0.0f
    var takeMoney = 0.0f


    private val _salesMutableLiveData = MutableLiveData<List<SaleInfo>>()
    val salesLiveData: LiveData<List<SaleInfo>>
        get() = _salesMutableLiveData

    init {
        getDayInfo()
    }

    fun getDayInfo() {
        sendRequest(
            ApeniApiService.getInstance().getDayInfo("2019-05-12", 0),
            success = {
                val sales: ArrayList<SaleInfo> = ArrayList()
                Log.d(TAG, it.toString())
                _salesMutableLiveData.value = it.realizebuli
            },
            finally = {
                Log.d(TAG, "dayReq $it")
            }
        )
    }
}
