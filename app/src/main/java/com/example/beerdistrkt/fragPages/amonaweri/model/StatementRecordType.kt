package com.example.beerdistrkt.fragPages.amonaweri.model

import androidx.annotation.IntegerRes
import com.example.beerdistrkt.R
import com.squareup.moshi.Json

enum class StatementRecordType(@IntegerRes val icon: Int? = null) {

    @Json(name = "beer")
    SALE_BEER(R.drawable.barrel_24),

    @Json(name = "bottle")
    SALE_BOTTLE(R.drawable.beer_bottle),

    @Json(name = "money")
    TAKE_MONEY,

    @Json(name = "")
    NONE
}