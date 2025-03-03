package com.example.beerdistrkt.fragPages.settings.domain.model

import com.example.beerdistrkt.utils.DiffItem

data class SettingParam(
    val code: SettingCode,
    val value: Int
) : DiffItem {

    override val key: Any
        get() = code
}
