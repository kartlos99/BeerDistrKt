package com.example.beerdistrkt.models

class Order(
    val ID: Int,
    val orderDate: String,
    val orderStatusID: Int,
    val distributorID: Int,
    val clientID: Int,
    val client: Obieqti,
    val comment: String?,
    val modifyDate: String,
    val modifyUserID: Int,
    val items: List<Item>,
    val sales: List<Sales>
) {
    data class Item(
        val ID: Int,
        val orderID: Int,
        val beerID: Int,
        val beer: BeerModel,
        val canTypeID: Int,
        val count: Int,
        val check: Int,
        val modifyDate: String,
        val modifyUserID: Int
    )

    data class Sales(
        val orderID: Int,
        val beerID: Int,
        val check: Int,
        val canTypeID: Int,
        val count: Int
    )
}