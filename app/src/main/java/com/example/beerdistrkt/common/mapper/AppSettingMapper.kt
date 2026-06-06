package com.example.beerdistrkt.common.mapper

import com.example.beerdistrkt.common.domain.model.AppSetting
import com.example.beerdistrkt.common.model.SettingDto
import javax.inject.Inject

class AppSettingMapper @Inject constructor() {

    fun mapToDomain(item: SettingDto): AppSetting {

        return AppSetting(
            id = item.id,
            code = item.code,
            boolValue = item.valueText?.toBooleanStrictOrNull() ?: (item.valueText == "1"),
            stringValue = item.valueText,
        )
    }
}