package com.example.beerdistrkt.common.domain.model

import com.squareup.moshi.Json

enum class EntityStatus(val value: String) {
    @Json(name = "0")
    DELETED("0"),

    @Json(name = "1")
    ACTIVE("1"),

    @Json(name = "2")
    INACTIVE("2");

    companion object {
        fun fromValue(value: String): EntityStatus? {
            return entries.firstOrNull {
                it.value == value
            }
        }
    }
}