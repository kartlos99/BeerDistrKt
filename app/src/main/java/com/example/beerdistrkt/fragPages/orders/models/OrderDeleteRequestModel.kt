package com.example.beerdistrkt.fragPages.orders.models

data class OrderDeleteRequestModel(
    val orderID: Int,
    var modifyUserID: String
)