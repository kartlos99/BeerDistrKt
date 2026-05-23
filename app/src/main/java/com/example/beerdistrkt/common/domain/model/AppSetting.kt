package com.example.beerdistrkt.common.domain.model

data class AppSetting(
    val id: String,
    val code: String,
    val boolValue: Boolean?,
    val stringValue: String?,
)
