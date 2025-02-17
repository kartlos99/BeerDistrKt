package com.example.beerdistrkt.models

import com.squareup.moshi.Json

data class MappedUser(
    val ID: Int,
    val userID: Int,
    val regionID: Int,
    val regionName: String,
    val username: String,
    val userDisplayName: String,
    val userStatus: UserStatus
) {
    val isActive: Boolean
        get() = userStatus == UserStatus.ACTIVE
}

enum class UserStatus(val value: String) {
    @Json(name = "0")
    DELETED("0"),

    @Json(name = "1")
    ACTIVE("1"),

    @Json(name = "2")
    INACTIVE("2"),
}