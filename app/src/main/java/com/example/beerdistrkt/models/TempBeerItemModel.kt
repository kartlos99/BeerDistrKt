package com.example.beerdistrkt.models

import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel
import com.example.beerdistrkt.fragPages.sales.models.SaleRequestModel

data class TempBeerItemModel(
    val ID: Int = 0,
    val beer: BeerModel,
    val canType: CanModel,
    val count: Int,
    val onRemoveClick: (beerItem: TempBeerItemModel) -> Unit,
    val orderItemID: Int = 0,
    val onEditClick: (() -> Unit)? = null
) {

    fun toRequestOrderItem(orderCheck: Boolean, userID: String): OrderRequestModel.Item {
        return OrderRequestModel.Item(
            ID = ID,
            orderID = orderItemID,
            beerID = beer.id,
            canTypeID = canType.id,
            count = count,
            check = orderCheck,
            modifyUserID = userID
        )
    }

    fun toRequestSaleItem(
        saleDate: String,
        orderID: Int
    ): SaleRequestModel.SaleItem {
        return SaleRequestModel.SaleItem(
            ID = ID,
            saleDate = saleDate,
            beerID = beer.id,
            price = beer.fasi ?: .0,
            canTypeID = canType.id,
            count = count,
            orderID = orderID
        )
    }
}