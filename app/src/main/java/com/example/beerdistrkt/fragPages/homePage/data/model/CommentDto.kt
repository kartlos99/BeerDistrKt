package com.example.beerdistrkt.fragPages.homePage.data.model

import androidx.annotation.Keep

@Keep
data class CommentDto(
    val comment: String,
    val op: Char,
    val commentDate: String,
    val customerName: String,
    val username: String,
)