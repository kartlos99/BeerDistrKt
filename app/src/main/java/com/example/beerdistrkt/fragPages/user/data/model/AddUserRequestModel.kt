package com.example.beerdistrkt.fragPages.user.data.model

import com.example.beerdistrkt.fragPages.user.domain.model.User

data class AddUserRequestModel(
    val user: User,
    val password: String = "",
    val changePass: Boolean = false,
    val regionIDs: Set<Int>,
)