package com.example.beerdistrkt.fragPages.bottle.presentation.model

import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel
import com.example.beerdistrkt.fragPages.realisationtotal.models.SaleRequestModel

data class TempBottleItemModel(
    val id: Int = 0,
    val bottle: Bottle,
    val count: Int,
    val onRemoveClick: (bottleItem: TempBottleItemModel) -> Unit,
    val orderItemID: Int = 0,
    val onEditClick: ((bottleItem: TempBottleItemModel) -> Unit)? = null
) {

    fun toRequestOrderItem(orderCheck: Boolean): OrderRequestModel.BottleItem {
        return OrderRequestModel.BottleItem(
            ID = orderItemID,
            orderID = id,
            bottleID = bottle.id,
            count = count,
            check = orderCheck,
        )
    }

    fun toRequestSaleItem(
        isGift: Boolean = false
    ): SaleRequestModel.BottleSaleItem {

        return SaleRequestModel.BottleSaleItem(
            id = id,
            bottleID = bottle.id,
            price = if (isGift) .0 else bottle.price,
            count = count
        )
    }
}