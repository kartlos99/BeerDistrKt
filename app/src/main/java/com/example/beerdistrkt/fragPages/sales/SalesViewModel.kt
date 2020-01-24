package com.example.beerdistrkt.fragPages.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.SaleInfo
import com.example.beerdistrkt.network.ApeniApiService

class SalesViewModel : BaseViewModel() {

    val TAG = "ordersVM"
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
            ApeniApiService.getInstance().getDayInfo("2019-07-11", 0),
            success = {
                val sales: ArrayList<SaleInfo> = ArrayList()
                for (i in 0..it.size - 3) {
                    sales.add(SaleInfo.mapToSaleInfo(it[i]))
                }
                takeMoney = it[it.size - 2]["money"]?.toFloatOrNull() ?: 0f

                k30empty = it[it.size - 1]["k30"]?.toFloatOrNull() ?: 0f
                k50empty = it[it.size - 1]["k50"]?.toFloatOrNull() ?: 0f

                _salesMutableLiveData.value = sales
            },
            finally = {
                Log.d(TAG, "dayReq $it")
            }
        )
    }
}
