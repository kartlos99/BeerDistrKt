package com.example.beerdistrkt.models

data class DeleteRequest(
    val recordID: String,
    val table: String,
    val userID: String
)
