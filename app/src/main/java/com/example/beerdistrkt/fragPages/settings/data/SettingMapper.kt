package com.example.beerdistrkt.fragPages.settings.data

import com.example.beerdistrkt.fragPages.settings.data.model.SettingParamDto
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import javax.inject.Inject

class SettingMapper @Inject constructor() {

    fun toDomain(dto: SettingParamDto): SettingParam {
        return SettingParam(
            code = dto.code,
            value = dto.paramValue
        )
    }

    fun toDto(settingParam: SettingParam): SettingParamDto {
        return SettingParamDto(
            id = 0,
            code = settingParam.code,
            paramValue = settingParam.value
        )
    }
}