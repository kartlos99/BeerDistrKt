package com.example.beerdistrkt.fragPages.orders.models

data class OrderRequestModel(
    val ID: Int = 0,
    val orderDate: String,
    val orderStatus: Int,
    val distributorID: Int,
    val clientID: Int,
    val regionID: Int,
    val comment: String? = null,
    val modifyUserID: String,
    val items: List<Item>,
    val bottleItems: List<BottleItem>
) {

    data class Item(
        val ID: Int = 0,
        val orderID: Int = 0,
        val beerID: Int,
        val canTypeID: Int,
        val count: Int,
        val check: Boolean,
    )

    data class BottleItem(
        val ID: Int = 0,
        val orderID: Int = 0,
        val bottleID: Int,
        val count: Int,
        val check: Boolean,
    )
}