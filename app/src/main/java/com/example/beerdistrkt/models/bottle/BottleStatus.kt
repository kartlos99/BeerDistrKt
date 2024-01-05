package com.example.beerdistrkt.models.bottle

import com.squareup.moshi.Json

enum class BottleStatus(val value: String) {

    @Json(name = "0")
    DELETED("0"),

    @Json(name = "1")
    ACTIVE("1"),

    @Json(name = "2")
    INACTIVE("2"),

    @Json(name = "3")
    DELETED_BY_USER("3"),
}