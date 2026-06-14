package com.example.beerdistrkt.fragPages.barrel.data.model

import com.example.beerdistrkt.models.OrderStatus

data class EmptyBarrelsDtoWrapper(
    val barrelInfo: List<EmptyBarrelsInfoDto>,
)

data class EmptyBarrelsInfoDto(
    val orderID: Int,
    val clientID: Int,
    val distributorID: Int,
    val orderStatus: OrderStatus,
    val orderDate: String,
    val barrels: List<EmptyBarrelsIoDto>,
)

data class EmptyBarrelsIoDto(
    val barrelId: Int,
    val inputCount: Int,
    val outputCount: Int,
)
