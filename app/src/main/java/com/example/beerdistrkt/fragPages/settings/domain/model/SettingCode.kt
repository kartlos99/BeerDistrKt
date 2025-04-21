package com.example.beerdistrkt.fragPages.settings.domain.model

import androidx.annotation.StringRes
import com.example.beerdistrkt.R
import com.squareup.moshi.Json

enum class SettingCode(@StringRes val displayName: Int) {
    @Json(name = "customer_idle_warning")
    IDLE_WARNING(R.string.customer_idle_setting_title),

    @Json(name = "object_cleaning_warning")
    CLEANING_WARNING_FOR_CUSTOMER(R.string.customer_cleaning_setting_title),

    @Json(name = "object_cleaning_warning_R")
    CLEANING_WARNING_FOR_RESTAURANT_CUSTOMER(R.string.customer_r_cleaning_setting_title)
}