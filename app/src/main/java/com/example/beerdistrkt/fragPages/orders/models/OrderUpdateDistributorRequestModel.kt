package com.example.beerdistrkt.fragPages.orders.models

data class OrderUpdateDistributorRequestModel(
    var orderID: Int,
    var distributorID: String
) {
}