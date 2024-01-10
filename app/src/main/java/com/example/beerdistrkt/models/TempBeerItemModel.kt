package com.example.beerdistrkt.models

import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel
import com.example.beerdistrkt.fragPages.realisationtotal.models.SaleRequestModel

data class TempBeerItemModel(
    val ID: Int = 0,
    val beer: BeerModelBase,
    val canType: CanModel,
    val count: Int,
    val onRemoveClick: (beerItem: TempBeerItemModel) -> Unit,
    val orderItemID: Int = 0,
    val onEditClick: ((beerItem: TempBeerItemModel) -> Unit)? = null
) {

    fun identity() {

    }

    fun toRequestOrderItem(orderCheck: Boolean): OrderRequestModel.Item {
        return OrderRequestModel.Item(
            ID = orderItemID,
            orderID = ID,
            beerID = beer.id,
            canTypeID = canType.id,
            count = count,
            check = orderCheck,
        )
    }

    fun toRequestSaleItem(
        saleDate: String,
        orderID: Int,
        isGift: Boolean = false
    ): SaleRequestModel.SaleItem {

        return SaleRequestModel.SaleItem(
            ID = ID,
            saleDate = saleDate,
            beerID = beer.id,
            price = if (isGift) 0.0 else beer.fasi ?: .0,
            canTypeID = canType.id,
            count = count,
            orderID = orderID
        )
    }
}