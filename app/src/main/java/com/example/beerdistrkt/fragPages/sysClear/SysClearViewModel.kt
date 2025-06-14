package com.example.beerdistrkt.fragPages.sysClear

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomerUseCase
import com.example.beerdistrkt.fragPages.sysClear.models.AddClearingModel
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SysClearViewModel @Inject constructor(
    private val getCustomerUseCase: GetCustomerUseCase,
) : BaseViewModel() {

    private val _sysClearLiveData = MutableLiveData<ApiResponseState<List<SysClearModel>>>()
    val sysClearLiveData: LiveData<ApiResponseState<List<SysClearModel>>> by ::_sysClearLiveData

    private val _addClearFlow = MutableSharedFlow<ApiResponseState<String>>()
    val addClearFlow: SharedFlow<ApiResponseState<String>> = _addClearFlow

    init {
        getSysCleanList()
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
            val requestData = AddClearingModel(deleteRecordID, clientID, session.getUserID())
            sendRequest(
                ApeniApiService.getInstance().addDeleteClearing(requestData),
                successWithData = {
//                    Log.d(TAG, "addClearingData: req2")
                    viewModelScope.launch {
                        _addClearFlow.emit(ApiResponseState.Success(it))
                    }
                    getSysCleanList()
                },
                finally = {
                    viewModelScope.launch { _addClearFlow.emit(ApiResponseState.Loading(false)) }
                }
            )
        }
    }

    suspend fun findClient(clientID: Int) =
        getCustomerUseCase(clientID)
}
