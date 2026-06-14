package com.example.beerdistrkt.fragPages.barrel.domain.model

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.models.OrderStatus

data class EmptyBarrelsInfo(
    val orderID: Int,
    val client: Customer,
    val distributor: User,
    val orderStatus: OrderStatus,
    val orderDate: String,
    val barrels: List<EmptyBarrelsIo>,
)

data class EmptyBarrelsIo(
    val barrel: Barrel,
    val inputCount: Int,
    val outputCount: Int,
)