package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.launch
import java.util.*

class OrdersViewModel : BaseViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val clientsLiveData = database.getAllObieqts()
    private val beersLiveData = database.getBeerList()

    val ordersLiveData = MutableLiveData<ApiResponseState<List<Order>>>()

    private lateinit var clients: List<Obieqti>
    private lateinit var beers: List<BeerModel>

    var orderDateCalendar: Calendar = Calendar.getInstance()

    private val _orderDayLiveData = MutableLiveData<String>()
    val orderDayLiveData: LiveData<String>
        get() = _orderDayLiveData

    init {
        getOrders(dateFormatDash.format(orderDateCalendar.time))
        clientsLiveData.observeForever {
            clients = it
        }
        beersLiveData.observeForever {
            beers = it
            Log.d("Beer", beers.toString())
        }
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
    }


    private fun getOrders(date: String) {
        Log.d(TAG, date)
        ordersLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getOrders(date),
            successWithData = {
                ordersLiveData.value = ApiResponseState.Success(
                    it.map { orderDTO -> orderDTO.toPm(clients, beers) }
                )
            },
            failure = {
                Log.d("getOrder", "failed: ${it.message}")
            },
            finally = {
                ordersLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        orderDateCalendar.set(year, month, day)
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
        getOrders(dateFormatDash.format(orderDateCalendar.time))
    }


    companion object {
        const val TAG = "ordersVM"
    }
}
