package com.example.beerdistrkt.fragPages.showHistory

data class OrderHistoryDTO(
    val hID: Int,
    val ID: Int,
    val orderDate: String,
    val orderStatusID: Int,
    val distributorID: Int,
    val clientID: Int,
    val comment: String,
    val modifyDate: String,
    val modifyUserID: Int,
    val items: List<Item>? = null
) {

    data class Item(
        val hID: Int,
        val ID: Int,
        val orderID: Int,
        val beerID: Int,
        val canTypeID: Int,
        val count: Int,
        val chek: Int,
        val modifyDate: String,
        val modifyUserID: Int,
        val disrupterUserID: Int
    )
}