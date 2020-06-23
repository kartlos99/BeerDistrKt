package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.launch
import java.util.*

class AddOrdersViewModel(private val clientID: Int) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    val beerList = ObjectCache.getInstance().getList(BeerModel::class, "beerList")
        ?: mutableListOf()
    val cansList = ObjectCache.getInstance().getList(CanModel::class, "canList")
        ?: listOf()
    val usersList = ObjectCache.getInstance().getList(User::class, "userList")
        ?: listOf()

    var selectedCan: CanModel? = null
    var selectedDistributorID: Int = 0

    val orderItemsList = mutableListOf<TempBeerItemModel>()
    val orderItemsLiveData = MutableLiveData<List<TempBeerItemModel>>()

    var orderDateCalendar: Calendar = Calendar.getInstance()

    private val _orderDayLiveData = MutableLiveData<String>()
    val orderDayLiveData: LiveData<String>
        get() = _orderDayLiveData

    init {
        Log.d("addOrderVM", clientID.toString())
        getClient()
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getObiectsWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

    fun setCan(pos: Int) {
        selectedCan = if (pos >= 0)
            cansList[pos]
        else
            null
    }

    fun addOrderItemToList(item: TempBeerItemModel) {
        orderItemsList.add(item)
        orderItemsLiveData.value = orderItemsList
    }

    fun removeOrderItemFromList(item: TempBeerItemModel) {
        orderItemsList.remove(item)
        orderItemsLiveData.value = orderItemsList
    }

    fun addOrder(comment: String, isChecked: Boolean) {

        val orderRequestModel = OrderRequestModel(
            0,
            dateFormatDash.format(orderDateCalendar.time),
            OrderStatus.ACTIVE.data,
            selectedDistributorID,
            clientID,
            comment,
            Session.get().userID ?: "0",
            orderItemsList.map { it.toRequestOrderItem(isChecked, Session.get().userID ?: "0") }
        )

        sendRequest(
            ApeniApiService.getInstance().addOrder(orderRequestModel),
            successWithData = {
                Log.d("itemCount", it)
            }
        )
    }

    fun onOrderDateSelected(year: Int, month: Int, day: Int) {
        orderDateCalendar.set(year, month, day)
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
    }
}
