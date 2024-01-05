package com.example.beerdistrkt.models.bottle.dto

import com.example.beerdistrkt.models.bottle.BottleStatus
import com.squareup.moshi.Json

data class BaseBottleModelDto(
    val id: Int,
    val name: String,
    val volume: Double,
    val actualVolume: Double,
    val beerID: Int,
    val price: Double,
    val status: BottleStatus,
    val sortValue: String,
    @Json(name = "image")
    val imageFileName: String?
)
