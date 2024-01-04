package com.example.beerdistrkt.models.bottle

import com.example.beerdistrkt.fragPages.realisationtotal.models.SaleRequestModel

data class TempBottleItemModel(
    val id: Int = 0,
    val bottle: BaseBottleModel,
    val count: Int,
    val onRemoveClick: (bottleItem: TempBottleItemModel) -> Unit,
    val orderItemID: Int = 0,
    val onEditClick: ((bottleItem: TempBottleItemModel) -> Unit)? = null
) {

    fun toRequestSaleItem(
        isGift: Boolean = false
    ): SaleRequestModel.BottleSaleItem {

        return SaleRequestModel.BottleSaleItem(
            bottleID = bottle.id,
            price = if (isGift) .0 else bottle.price,
            count = count
        )
    }
}