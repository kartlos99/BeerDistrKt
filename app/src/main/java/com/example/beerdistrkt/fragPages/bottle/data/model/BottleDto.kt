package com.example.beerdistrkt.fragPages.bottle.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.fragPages.bottle.domain.model.BottleStatus
import com.squareup.moshi.Json

@Keep
data class BottleDto(
    val id: Int,
    val name: String,
    val volume: Double,
    val actualVolume: Double,
    val beerID: Int,
    val price: Double,
    val status: BottleStatus,
    val sortValue: Double,
    @Json(name = "image")
    val imageFileName: String?
) {
    constructor(
        bottleID: Int,
        name: String,
        volume: Double,
        beerID: Int,
        price: Double,
        status: BottleStatus
    ) : this(
        bottleID,
        name,
        volume,
        volume,
        beerID,
        price,
        status,
        .0,
        null
    )
}
