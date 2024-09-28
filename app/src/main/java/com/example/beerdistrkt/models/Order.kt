package com.example.beerdistrkt.models

import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.TempBottleItemModel
import com.squareup.moshi.Json

data class Order(
    val ID: Int,
    val orderDate: String,
    var orderStatus: OrderStatus,
    val distributorID: Int,
    val clientID: Int,
    val client: Obieqti,
    val comment: String?,
    val isEdited: Int,
    var sortValue: Double,
    val modifyDate: String,
    val modifyUserID: Int,
    val needCleaning: Int,
    val passDays: Int,
    val items: List<Item>,
    val bottleItems: List<BottleItem>,
    val sales: List<Sales>,
    val bottleSales: List<BottleSaleItem>,
    private val _onDeleteClick: (Order) -> Unit,
    private val _onEditClick: (Order) -> Unit,
    private val _onChangeDistributorClick: ((Order) -> Unit)? = null,
    private val _onItemClick: ((Order) -> Unit)? = null,
    private val _onHistoryClick: ((String) -> Unit)? = null
) {
    val hasAnyItemToShow: Boolean
        get() = items.isNotEmpty() || sales.isNotEmpty()
                || bottleItems.isNotEmpty() || bottleSales.isNotEmpty()

    val onDeleteClick = {
        _onDeleteClick(this)
    }
    val onEditClick = {
        _onEditClick(this)
    }
    val onChangeDistributorClick = {
        _onChangeDistributorClick?.invoke(this)
    }
    val onItemClick = {
        _onItemClick?.invoke(this)
    }
    val onHistoryClick = {
        _onHistoryClick?.invoke(this.ID.toString())
    }

    data class Item(
        val ID: Int,
        val orderID: Int,
        val beerID: Int,
        val beer: BeerModelBase,
        val canTypeID: Int,
        val count: Int,
        val check: Int,
        val modifyDate: String,
        val modifyUserID: Int
    ) {

        fun toTempBeerItemModel(
            barrels: List<CanModel>,
            onRemove: (beerItem: TempBeerItemModel) -> Unit,
            onEdit: (beerItem: TempBeerItemModel) -> Unit
        ): TempBeerItemModel? {
            val canType = barrels.find { it.id == canTypeID }
            return takeUnless { canType == null }?.let {
                TempBeerItemModel(
                    ID = orderID,
                    beer = beer,
                    canType = canType!!,
                    count = count,
                    orderItemID = ID,
                    onRemoveClick = onRemove,
                    onEditClick = onEdit
                )
            }
        }
    }

    data class Sales(
        val orderID: Int,
        val beerID: Int,
        val beer: BeerModelBase,
        val check: Int,
        val canTypeID: Int,
        val count: Int
    )

    data class BottleItem(
        val id: Int,
        val orderID: Int,
        val bottle: BaseBottleModel,
        val count: Int,
    ) {
        fun toTempBottleItemModel(
            onRemove: (bottleItem: TempBottleItemModel) -> Unit,
            onEdit: (bottleItem: TempBottleItemModel) -> Unit,
        ): TempBottleItemModel {
            
            return TempBottleItemModel(
                id = orderID,
                bottle = bottle,
                count = count,
                orderItemID = id,
                onRemoveClick = onRemove,
                onEditClick = onEdit,
            )
        }
    }

    data class BottleSaleItem(
        val orderID: Int,
        val bottle: BaseBottleModel,
        val count: Int,
    )

    fun isChecked() = items.any { it.check == 1 }

    fun getRemainingOrderItems(): List<Item> {
        val result = mutableListOf<Item>()

        items.forEach { item ->
            val deliveredCount = sales.find { sale ->
                item.beerID == sale.beerID && item.canTypeID == sale.canTypeID
            }?.count ?: 0
            val diff = if (item.count - deliveredCount < 0) 0 else item.count - deliveredCount
            val copyItem = item.copy(count = diff)
            result.add(copyItem)
        }

        return result
    }

    fun getBottleOrderItemsToDisplay(): List<BottleItem> {
        return bottleItems.toMutableList().apply {
            val keys = map { it.bottle.id }
            bottleSales
                .filter { keys.contains(it.bottle.id).not() }
                .forEach {
                    add(
                        BottleItem(
                            0,
                            it.orderID,
                            it.bottle,
                            0
                        )
                    )
                }
        }
    }
}

enum class OrderStatus(val textRes: Int, val data: Int) {
    @Json(name = "order_active")
    ACTIVE(R.string.active, 1),

    @Json(name = "order_completed")
    COMPLETED(R.string.completed, 2),

    @Json(name = "order_canceled")
    CANCELED(R.string.canceled, 3),

    @Json(name = "order_deleted")
    DELETED(R.string.is_deleted, 4),

    @Json(name = "order_auto_created")
    AUTO_CREATED(R.string.auto_created, 5)
}