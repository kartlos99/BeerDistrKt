package com.example.beerdistrkt.fragPages.settings.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingCode

@Keep
data class SettingParamDto(
    val id: Int,
    val code: SettingCode,
    val paramValue: Int,
)
