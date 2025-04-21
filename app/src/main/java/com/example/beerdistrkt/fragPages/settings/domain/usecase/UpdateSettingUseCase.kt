package com.example.beerdistrkt.fragPages.settings.domain.usecase

import com.example.beerdistrkt.fragPages.settings.domain.SettingsRepository
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import com.example.beerdistrkt.network.model.ResultState
import javax.inject.Inject

class UpdateSettingUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(settingParam: SettingParam): ResultState<List<SettingParam>> =
        repository.updateSetting(settingParam)
}