package com.example.beerdistrkt.fragPages.orders.models

data class OrderRequestModel(
    val ID: Int = 0,
    val orderDate: String,
    val orderStatus: Int,
    val distributorID: Int,
    val clientID: Int,
    val comment: String? = null,
    val modifyUserID: String,
    val items: List<Item>
) {

    data class Item(
        val ID: Int = 0,
        val orderID: Int = 0,
        val beerID: Int,
        val canTypeID: Int,
        val count: Int,
        val check: Boolean,
        val modifyUserID: String
    )
}