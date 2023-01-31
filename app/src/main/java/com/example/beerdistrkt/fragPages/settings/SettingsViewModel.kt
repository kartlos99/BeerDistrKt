package com.example.beerdistrkt.fragPages.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.settings.model.SettingParam
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState

class SettingsViewModel : BaseViewModel() {

    private val _getSettingsLiveData = MutableLiveData<ApiResponseState<List<SettingParam>>>()
    val getSettingsLiveData: LiveData<ApiResponseState<List<SettingParam>>>
        get() = _getSettingsLiveData

    private val _updateValuesLiveData = MutableLiveData<ApiResponseState<String>>()
    val updateValuesLiveData: LiveData<ApiResponseState<String>>
        get() = _updateValuesLiveData

    init {
        _getSettingsLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getSettingsValue(),
            successWithData = {
                _getSettingsLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                _getSettingsLiveData.value = ApiResponseState.Loading(false)
                if (!it)
                    _getSettingsLiveData.value = ApiResponseState.ApiError(1, "settingsFail")
            }
        )
    }

    fun setNewValue(code: SettingCode, value: String) {
        _updateValuesLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().updateSettingsValue(
                SettingParam(code.code, if (value.isBlank()) 0 else value.toInt())
            ),
            successWithData = {
                _updateValuesLiveData.value = ApiResponseState.Success(value)
            },
            finally = {
                _updateValuesLiveData.value = ApiResponseState.Loading(false)
                if (!it)
                    _updateValuesLiveData.value = ApiResponseState.ApiError(1, "update Fail")
            }
        )
    }

    enum class SettingCode(val code: String) {
        IDLE_WARNING("customer_idle_warning"),
        CLEANING_WARNING("object_cleaning_warning")
    }
}