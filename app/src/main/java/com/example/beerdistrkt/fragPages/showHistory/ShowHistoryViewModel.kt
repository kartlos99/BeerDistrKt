package com.example.beerdistrkt.fragPages.showHistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState

class ShowHistoryViewModel : BaseViewModel() {
    private val TAG = "ShowHistoryViewModel"

    private val clientsLiveData = database.getAllObieqts()
    private val userLiveData = database.getUsers()

    private lateinit var clients: List<Obieqti>
    private lateinit var usersList: List<User>


    private val beerLiveData = database.getBeerList()
    lateinit var beerMap: Map<Int, BeerModel>

    private val _orderHistoryLiveData = MutableLiveData<ApiResponseState<List<OrderHistory>>>()
    val orderHistoryLiveData: LiveData<ApiResponseState<List<OrderHistory>>>
        get() = _orderHistoryLiveData

    init {
        beerLiveData.observeForever { beerList ->
            beerMap = beerList.groupBy { it.id }.mapValues {
                it.value[0]
            }
        }
        clientsLiveData.observeForever { clients = it }
        userLiveData.observeForever { usersList = it }
    }

    fun getData(recordID: String) {
        sendRequest(
            ApeniApiService.getInstance().getOrderHistory(recordID),
            successWithData = {
                val pmHistory = it.map { dtoHistoryModel ->
                    dtoHistoryModel.toOrderHistory(clients, usersList)
                }
                _orderHistoryLiveData.value = ApiResponseState.Success(pmHistory)
            },
            finally = {
                Log.d(TAG, "getData: finaly = $it")
            }
        )
    }

}