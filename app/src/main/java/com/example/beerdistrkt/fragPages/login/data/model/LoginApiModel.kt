package com.example.beerdistrkt.fragPages.login.data.model

import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.fragPages.user.data.model.WorkRegionDto

data class LoginApiModel(
    val id: Int,
    val username: String,
    val name: String,
    val type: UserType,
    val permissions: List<Permission>,
    val regions: List<WorkRegionDto>,
    val token: String
)
