package com.example.beerdistrkt.fragPages.settings.domain.model

import com.squareup.moshi.Json

enum class SettingCode {
    @Json(name = "customer_idle_warning")
    IDLE_WARNING,

    @Json(name = "object_cleaning_warning")
    CLEANING_WARNING_FOR_CUSTOMER,

    @Json(name = "object_cleaning_warning_R")
    CLEANING_WARNING_FOR_RESTAURANT_CUSTOMER
}