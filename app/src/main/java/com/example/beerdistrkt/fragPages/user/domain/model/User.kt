package com.example.beerdistrkt.fragPages.user.domain.model

import com.example.beerdistrkt.fragPages.login.domain.model.UserType
import com.example.beerdistrkt.models.UserStatus

data class User(
    val id: String,
    val username: String,
    val name: String,
    val type: UserType,
    val tel: String,
    val address: String,
    val maker: String,
    val comment: String,
    val userStatus: UserStatus,
    val regions: List<WorkRegion>,
) {
    val isActive: Boolean
        get() = userStatus == UserStatus.ACTIVE
}
