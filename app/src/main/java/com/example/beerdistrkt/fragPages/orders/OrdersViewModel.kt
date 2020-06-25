package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.fragPages.orders.models.OrderDeleteRequestModel
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

    val askForOrderDeleteLiveData = MutableLiveData<Order?>(null)
    val orderDeleteLiveData = MutableLiveData<ApiResponseState<Int>>()

    val listOfOrders: MutableList<Order> = mutableListOf()

    init {
        clientsLiveData.observeForever {
            clients = it
        }
        beersLiveData.observeForever {
            beers = it
            Log.d("Beer", beers.toString())
        }
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
    }


    fun getOrders() {

        ordersLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getOrders(dateFormatDash.format(orderDateCalendar.time)),
            successWithData = {
                listOfOrders.clear()
                listOfOrders.addAll(it.map { orderDTO ->
                    orderDTO.toPm(
                        clients,
                        beers,
                        onDeleteClick = {order ->
                            askForOrderDeleteLiveData.value = order
                        },
                        onEditClick = ::editOrder
                    )
                })
                ordersLiveData.value = ApiResponseState.Success(listOfOrders)
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
        getOrders()
    }

    fun deleteOrder(order: Order) {
        Log.d("onDelete", order.toString())
        sendRequest(
            ApeniApiService.getInstance().deleteOrder(OrderDeleteRequestModel(order.ID)),
            success = {
                val index = listOfOrders.indexOf(order)
                listOfOrders.remove(order)
                orderDeleteLiveData.value = ApiResponseState.Success(index)
            }
        )
    }

    private fun editOrder(order: Order) {
        Log.d("onEdit", order.toString())
    }

    companion object {
        const val TAG = "ordersVM"
    }
}
