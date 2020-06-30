package com.example.beerdistrkt.fragPages.orders.models

import com.example.beerdistrkt.models.Order

data class OrderGroupModel(
    val distributorID: Int,
    val distributorName: String,
    val ordersList: MutableList<Order>,
    var isExpanded: Boolean = true

) {
}