package com.example.beerdistrkt.fragPages.showHistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomersUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUsersUseCase
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowHistoryViewModel @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
) : BaseViewModel() {

    lateinit var beerMap: Map<Int, Beer>

    private val _orderHistoryLiveData = MutableLiveData<ApiResponseState<List<OrderHistory>>>()
    val orderHistoryLiveData: LiveData<ApiResponseState<List<OrderHistory>>>
        get() = _orderHistoryLiveData

    val infoEventsFlow: MutableSharedFlow<String> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            beerMap = getBeerUseCase()
                .groupBy { it.id }
                .mapValues { it.value[0] }
        }
    }

    fun getData(recordID: String) {
        sendRequest(
            ApeniApiService.getInstance().getOrderHistory(recordID),
            successWithData = {
                viewModelScope.launch {
                    val pmHistory = it.mapNotNull { dtoHistoryModel ->
                        dtoHistoryModel.toOrderHistory(
                            getCustomersUseCase(),
                            getUsersUseCase()
                        )
                    }
                    _orderHistoryLiveData.value = ApiResponseState.Success(pmHistory)
                    if (it.size != pmHistory.size)
                        infoEventsFlow.emit("incorrect mapping, some records are missed!")
                }
            },
            finally = {
                Log.d(TAG, "getData: finaly = $it")
            }
        )
    }

    companion object {
        private const val TAG = "ShowHistoryViewModel"
    }
}