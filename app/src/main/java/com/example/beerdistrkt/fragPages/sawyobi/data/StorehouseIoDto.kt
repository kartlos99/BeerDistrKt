package com.example.beerdistrkt.fragPages.sawyobi.data

import com.squareup.moshi.Json

data class StorehouseIoDto(
    val groupID: String,
    val ioDate: String,
    val distributorID: Int,
    @Json(name = "chek")
    val check: Int,
    val comment: String?,
    val barrelInput: List<BarrelInputDto>? = null,
    val barrelOutput: List<BarrelOutputDto>? = null,
    val bottleInput: List<BottleInputDto>? = null,
)

data class BarrelInputDto(
    val id: Int,
    val beerID: Int,
    val barrelID: Int,
    val count: Int,
)

data class BarrelOutputDto(
    val id: Int,
    val barrelID: Int,
    val count: Int,
)

data class BottleInputDto(
    val id: Int,
    val bottleID: Int,
    val count: Int,
)