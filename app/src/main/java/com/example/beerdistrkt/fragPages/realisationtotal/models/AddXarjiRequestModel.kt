package com.example.beerdistrkt.fragPages.realisationtotal.models

data class AddXarjiRequestModel(
    val distributorID: String,
    val amount: Double,
    val comment: String,
    val date: String
)