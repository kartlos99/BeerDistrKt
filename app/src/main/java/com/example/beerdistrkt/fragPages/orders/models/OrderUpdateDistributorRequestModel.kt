package com.example.beerdistrkt.fragPages.orders.models

data class OrderUpdateDistributorRequestModel(
    var orderID: Int,
    var distributorID: Int,
    var regionID: Int,
    var modifyUserID: String
) {
}