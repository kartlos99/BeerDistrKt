package com.example.beerdistrkt.fragPages.user.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.fragPages.login.domain.model.UserType
import com.example.beerdistrkt.models.UserStatus

@Keep
data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val type: UserType,
    val tel: String,
    val address: String,
    val maker: String,
    val comment: String,
    val userStatus: UserStatus,
    val regions: List<Int>,
)
