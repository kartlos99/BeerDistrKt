package com.example.beerdistrkt.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VcsResponse (
    val app: Int,
    val dbconf: Int,
    val dbinfo: Int,
    val beer: Int,
    val client: Int,
    val user: Int,
    val barrel: Int,
    val price: Int
)