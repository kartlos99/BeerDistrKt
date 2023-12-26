package com.example.beerdistrkt.models.bottle

data class TempBottleItemModel(
    val id: Int = 0,
    val bottle: BaseBottleModel,
    val count: Int,
    val onRemoveClick: (bottleItem: TempBottleItemModel) -> Unit,
    val orderItemID: Int = 0,
    val onEditClick: ((bottleItem: TempBottleItemModel) -> Unit)? = null
)
