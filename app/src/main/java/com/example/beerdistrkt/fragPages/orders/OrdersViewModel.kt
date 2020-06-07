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

class OrdersViewModel : BaseViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val clientsLiveData = database.getAllObieqts()
    private val beersLiveData = database.getBeerList()

    val ordersLiveData = MutableLiveData<ApiResponseState<List<Order>>>()

    private lateinit var clients: List<Obieqti>
    private lateinit var beers: List<BeerModel>

    init {
        getOrders()
        clientsLiveData.observeForever {
            clients = it
        }
        beersLiveData.observeForever {
            beers = it
            Log.d("Beer", beers.toString())
        }
    }


    private fun getOrders() {
        ordersLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getOrders("2020-04-14"),
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


    companion object {
        const val TAG = "ordersVM"
    }
}
