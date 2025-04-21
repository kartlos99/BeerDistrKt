package com.example.beerdistrkt.fragPages.settings.domain.usecase

import com.example.beerdistrkt.fragPages.settings.domain.SettingsRepository
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() = repository.getAllSetting()

    fun asFlow(): MutableStateFlow<ResultState<List<SettingParam>>> = repository.settingsFlow
}