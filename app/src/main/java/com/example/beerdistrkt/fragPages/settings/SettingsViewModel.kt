package com.example.beerdistrkt.fragPages.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingCode
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import com.example.beerdistrkt.fragPages.settings.domain.usecase.GetSettingsUseCase
import com.example.beerdistrkt.fragPages.settings.domain.usecase.UpdateSettingUseCase
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingUseCase: UpdateSettingUseCase,
) : BaseViewModel() {

    private val _updateValuesLiveData = MutableLiveData<ApiResponseState<String>>()
    val updateValuesLiveData: LiveData<ApiResponseState<String>>
        get() = _updateValuesLiveData

    private val _apiStateFlow: MutableStateFlow<ResultState<List<SettingParam>>> =
        getSettingsUseCase.asFlow()
    val apiStateFlow: StateFlow<ResultState<List<SettingParam>>> = _apiStateFlow.asStateFlow()


    fun setNewValue(code: SettingCode, value: String) {
        viewModelScope.launch {
            _apiStateFlow.emit(ResultState.Loading)
            _apiStateFlow.emit(
                updateSettingUseCase(
                    SettingParam(
                        code,
                        if (value.isBlank()) 0 else value.toInt()
                    )
                )
            )
        }
    }

}