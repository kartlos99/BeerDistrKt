package com.example.beerdistrkt.fragPages.sysClear

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sysClear.models.AddClearingModel
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SysClearViewModel : BaseViewModel() {

    private val clientListLiveData = database.getAllObieqts()

    private val _sysClearLiveData = MutableLiveData<ApiResponseState<List<SysClearModel>>>()
    val sysClearLiveData: LiveData<ApiResponseState<List<SysClearModel>>>
        get() = _sysClearLiveData

    private val _addClearFlow = MutableSharedFlow<ApiResponseState<String>>()
    val addClearFlow: SharedFlow<ApiResponseState<String>> = _addClearFlow

    private val customers = mutableListOf<Obieqti>()

    init {
        getSysCleanList()
        viewModelScope.launch {
            clientListLiveData.observeForever {
                customers.clear()
                customers.addAll(it)
            }
        }
    }

    private fun getSysCleanList() {
        _sysClearLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getSysCleaning(),
            successWithData = {
                _sysClearLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                _sysClearLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun addClearingData(clientID: Int, deleteRecordID: Int = 0) {
        viewModelScope.launch {
            _addClearFlow.emit(ApiResponseState.Loading(true))
            val requestData = AddClearingModel(deleteRecordID, clientID, Session.get().getUserID())
            sendRequest(
                ApeniApiService.getInstance().addDeleteClearing(requestData),
                successWithData = {
//                    Log.d(TAG, "addClearingData: req2")
                    uiScope.launch { _addClearFlow.emit(ApiResponseState.Success(it)) }
                    getSysCleanList()
                },
                finally = {
                    uiScope.launch { _addClearFlow.emit(ApiResponseState.Loading(false)) }
                }
            )
        }
    }

    fun findClient(clientID: Int) = customers.firstOrNull { it.id == clientID }
}
