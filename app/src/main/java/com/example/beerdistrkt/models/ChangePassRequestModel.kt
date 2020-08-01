package com.example.beerdistrkt.models

data class ChangePassRequestModel(
    val userID: String,
    val oldPass: String,
    val newPass: String
)