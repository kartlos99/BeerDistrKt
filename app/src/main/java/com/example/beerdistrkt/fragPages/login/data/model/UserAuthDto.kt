package com.example.beerdistrkt.fragPages.login.data.model

import androidx.annotation.Keep

@Keep
data class UserAuthDto(
    val exp: Long,
    val userID: Int,
    val userType: Int,
    val username: String,
    val regionID: Int,
)
