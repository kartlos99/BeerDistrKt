package com.example.beerdistrkt.models

data class TempOrderItemModel(
    val beer: BeerModel,
    val canType: CanModel,
    val count: Int,
    val onRemoveClick: (orderItem: TempOrderItemModel) -> Unit,
    val orderItemID: Int = 0,
    val onEditClick: (() -> Unit)? = null
) {
}