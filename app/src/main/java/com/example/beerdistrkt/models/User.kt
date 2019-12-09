package com.example.beerdistrkt.models

data class User(
    val id: String,
    val username: String,
    val name: String,
    val type: String,
    val tel: String,
    val adress: String,
    val maker: String,
    val comment: String
)