package com.example.beerdistrkt.fragPages.sysClear

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sysClear.models.AddClearingModel
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session

class SysClearViewModel : BaseViewModel() {

    var activeAdd = false

    val clientListLiveData = database.getAllObieqts()
    var selectedclientID = -1

    private val _sysClearLiveData = MutableLiveData<ApiResponseState<List<SysClearModel>>>()
    val sysClearLiveData: LiveData<ApiResponseState<List<SysClearModel>>>
        get() = _sysClearLiveData

    private val _addClearLiveData = MutableLiveData<ApiResponseState<String>>()
    val addClearLiveData: LiveData<ApiResponseState<String>>
        get() = _addClearLiveData

    init {
        getSysCleanList()
    }

    fun getSysCleanList() {
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

    fun selectClient(position: Int) {
        selectedclientID = (clientListLiveData.value?.get(position) as Obieqti).id ?: -1
    }

    fun addClearingData(recordID: Int = 0) {
        _addClearLiveData.value = ApiResponseState.Loading(true)
        val requestData = AddClearingModel(recordID, selectedclientID, Session.get().getUserID())
        sendRequest(
            ApeniApiService.getInstance().addDeleteClearing(requestData),
            successWithData = {
                _addClearLiveData.value = ApiResponseState.Success(it)
                getSysCleanList()
            },
            finally = {
                _addClearLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }
}
