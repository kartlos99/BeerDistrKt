package com.example.beerdistrkt.fragPages.barrel.presentation.model

import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelMatchStatus
import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsIo
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.utils.DiffItem

data class EmptyBarrelUiModel(
    val customerName: String,
    val orderStatus: OrderStatus,
    val barrelOutputStatus: EmptyBarrelMatchStatus,
    val barrels: List<EmptyBarrelsIo>,
): DiffItem
