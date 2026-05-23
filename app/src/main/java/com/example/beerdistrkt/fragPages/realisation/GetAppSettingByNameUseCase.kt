package com.example.beerdistrkt.fragPages.realisation

import com.example.beerdistrkt.common.repo.SharedRepository
import javax.inject.Inject

class GetAppSettingByNameUseCase @Inject constructor(
    private val repository: SharedRepository
) {
    suspend operator fun invoke(code: String): Boolean {
        return repository.getAppSettings().firstOrNull {
            it.code == code
        }?.boolValue ?: false
    }
}