package com.example.beerdistrkt.fragPages.statement.domain.model

import androidx.annotation.DrawableRes
import com.example.beerdistrkt.R
import com.squareup.moshi.Json

enum class StatementRecordType(@DrawableRes val icon: Int? = null) {

    @Json(name = "beer")
    SALE_BEER(R.drawable.ic_tank_600_24dp),

    @Json(name = "bottle")
    SALE_BOTTLE(R.drawable.beer_bottle),

    @Json(name = "money")
    TAKE_MONEY,

    @Json(name = "")
    NONE;

    fun isSaleType() = this == SALE_BEER || this == SALE_BOTTLE
}