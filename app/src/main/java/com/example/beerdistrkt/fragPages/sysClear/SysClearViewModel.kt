package com.example.beerdistrkt.fragPages.sysClear

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState

class SysClearViewModel : BaseViewModel() {

    private val _sysCleanLiveData = MutableLiveData<ApiResponseState<List<SysClearModel>>>()
    val sysCleanLiveData: LiveData<ApiResponseState<List<SysClearModel>>>
        get() = _sysCleanLiveData

    init {
        getSysCleanList()
    }

    fun getSysCleanList() {
        _sysCleanLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getSysCleaning(),
            successWithData = {
                _sysCleanLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                _sysCleanLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }
}
