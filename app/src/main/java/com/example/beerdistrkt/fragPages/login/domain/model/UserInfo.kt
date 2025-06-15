package com.example.beerdistrkt.fragPages.login.domain.model

import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion

data class UserInfo(
    val userID: String,
    val userType: UserType,
    val permissions: List<Permission>,
    val userName: String,
    val displayName: String,
    val accessToken: String,
    val accessTokenCreateTime: Long,
    val regions: List<WorkRegion>
)