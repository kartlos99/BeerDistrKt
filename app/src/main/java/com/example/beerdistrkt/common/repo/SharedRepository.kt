package com.example.beerdistrkt.common.repo

import com.example.beerdistrkt.common.domain.model.AppSetting
import com.example.beerdistrkt.common.mapper.AppSettingMapper
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class SharedRepository @Inject constructor(
    private val api: DistributionApi,
    private val appSettingMapper: AppSettingMapper,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseRepository(ioDispatcher) {

    private var appSettings: List<AppSetting> = emptyList()

    suspend fun getAppSettings(): List<AppSetting> {
        if (appSettings.isEmpty()) {
            fetchAppSettings()
        }
        return appSettings
    }

    suspend fun refresh() {
        fetchAppSettings()
    }

    private suspend fun fetchAppSettings() {
        apiCall {
            api.getAppSettings()
                .map(appSettingMapper::mapToDomain)
                .also {
                    appSettings = it
                }
        }
    }
}