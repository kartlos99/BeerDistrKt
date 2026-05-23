package com.example.beerdistrkt.fragPages.homePage.domain.usecase

import com.example.beerdistrkt.common.repo.SharedRepository
import javax.inject.Inject

class RefreshAppSettingsUseCase @Inject constructor(
    private val repository: SharedRepository
) {
    suspend operator fun invoke() = repository.refresh()
}