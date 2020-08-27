package com.example.beerdistrkt.fragPages.login.models

data class LoginResponse(
    val id: Int,
    val username: String,
    val name: String,
    val type: Int,
    val token: String
) {
}