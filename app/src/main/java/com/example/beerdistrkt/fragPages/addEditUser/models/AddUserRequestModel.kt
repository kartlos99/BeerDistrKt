package com.example.beerdistrkt.fragPages.addEditUser.models

import com.example.beerdistrkt.models.User

data class AddUserRequestModel(
    val user: User,
    val password: String = "",
    val changePass: Boolean = false
) {
}