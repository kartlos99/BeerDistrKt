package com.example.beerdistrkt.fragPages.user.data.model

import androidx.annotation.Keep

@Keep
data class UserApiModel(
    val users: List<UserDto>,
    val regions: List<WorkRegionDto>,
)
