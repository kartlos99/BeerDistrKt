package com.example.beerdistrkt.fragPages.settings.domain.usecase

import com.example.beerdistrkt.fragPages.settings.domain.SettingsRepository
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingCode
import com.example.beerdistrkt.orZero
import javax.inject.Inject

class GetSettingValueUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(settingCode: SettingCode) =
        settingsRepository.getSettingValue(settingCode).orZero()
}