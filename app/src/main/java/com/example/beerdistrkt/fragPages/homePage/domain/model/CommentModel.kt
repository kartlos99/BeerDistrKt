package com.example.beerdistrkt.fragPages.homePage.domain.model

data class CommentModel(
    val comment: String,
    val op: Char,
    val commentDate: String,
    val customerName: String,
    val username: String
)