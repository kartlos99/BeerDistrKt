package com.example.beerdistrkt.fragPages.settings

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import com.example.beerdistrkt.fragPages.settings.domain.usecase.GetSettingsUseCase
import com.example.beerdistrkt.fragPages.settings.domain.usecase.RefreshSettingsUseCase
import com.example.beerdistrkt.fragPages.settings.domain.usecase.UpdateSettingUseCase
import com.example.beerdistrkt.network.model.ResultState
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
    private val refreshSettingsUseCase: RefreshSettingsUseCase,
) : BaseViewModel() {

    private val _apiStateFlow: MutableStateFlow<ResultState<List<SettingParam>>> =
        getSettingsUseCase.asFlow()
    val apiStateFlow: StateFlow<ResultState<List<SettingParam>>> = _apiStateFlow.asStateFlow()


    fun setNewValue(setting: SettingParam, value: String) {
        viewModelScope.launch {
            _apiStateFlow.emit(ResultState.Loading)
            _apiStateFlow.emit(
                updateSettingUseCase(
                    settingParam = setting.copy(
                        value = if (value.isBlank()) 0 else value.toInt()
                    )
                )
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshSettingsUseCase()
        }
    }
}