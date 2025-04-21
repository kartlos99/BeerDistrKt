package com.example.beerdistrkt.fragPages.settings.data

import com.example.beerdistrkt.fragPages.settings.domain.SettingsRepository
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingCode
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class SettingsRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val settingMapper: SettingMapper,
    private val ioDispatcher: CoroutineDispatcher,
) : SettingsRepository, BaseRepository(ioDispatcher) {

    private val settingsMap = mutableMapOf<SettingCode, Int>()
    private var settings: List<SettingParam> = emptyList()

    override val settingsFlow: MutableStateFlow<ResultState<List<SettingParam>>> =
        MutableStateFlow(ResultState.Loading)

    override suspend fun getAllSetting(): List<SettingParam> {
        if (settings.isEmpty()) {
            fetchSettings()
        }
        return settings
    }

    override fun getSettingValue(settingCode: SettingCode): Int? = settingsMap[settingCode]

    override suspend fun updateSetting(settingParam: SettingParam): ResultState<List<SettingParam>> {
        return apiCall {
            api.updateSetting(settingMapper.toDto(settingParam))
            settingsMap[settingParam.code] = settingParam.value
            settingsMap.entries.map {
                SettingParam(
                    code = it.key,
                    value = it.value
                )
            }
        }
            .toResultState()
    }

    override suspend fun refresh() {
        fetchSettings()
    }

    private suspend fun fetchSettings() {
        settingsFlow.emit(ResultState.Loading)
        apiCall {
            api.getSettings()
                .map(settingMapper::toDomain)
                .also {
                    settings = it
                    settingsMap.clear()
                    it.forEach { param ->
                        settingsMap[param.code] = param.value
                    }
                }
        }.also { response ->
            settingsFlow.emit(response.toResultState())
        }
    }
}