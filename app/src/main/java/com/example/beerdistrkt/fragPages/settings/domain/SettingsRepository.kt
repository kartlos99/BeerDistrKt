package com.example.beerdistrkt.fragPages.settings.domain

import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.MutableStateFlow

interface SettingsRepository {
    suspend fun getAllSetting(): List<SettingParam>
    suspend fun updateSetting(settingParam: SettingParam): ResultState<List<SettingParam>>
    val settingsFlow: MutableStateFlow<ResultState<List<SettingParam>>>
    suspend fun refresh()
}