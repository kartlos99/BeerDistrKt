package com.example.beerdistrkt.fragPages.settings.domain.usecase

import com.example.beerdistrkt.fragPages.settings.domain.SettingsRepository
import javax.inject.Inject

class RefreshSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke() = repository.refresh()
}