package com.example.beerdistrkt.models

import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel

data class TempOrderItemModel(
    val ID: Int = 0,
    val beer: BeerModel,
    val canType: CanModel,
    val count: Int,
    val onRemoveClick: (orderItem: TempOrderItemModel) -> Unit,
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
}