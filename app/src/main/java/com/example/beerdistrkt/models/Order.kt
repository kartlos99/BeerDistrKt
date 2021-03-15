package com.example.beerdistrkt.models

import com.example.beerdistrkt.R
import com.squareup.moshi.Json

data class Order(
    val ID: Int,
    val orderDate: String,
    val orderStatus: OrderStatus,
    val distributorID: Int,
    val clientID: Int,
    val client: Obieqti,
    val comment: String?,
    val isEdited: Int,
    var sortValue: Double,
    val modifyDate: String,
    val modifyUserID: Int,
    val needCleaning: Int,
    val items: List<Item>,
    val sales: List<Sales>,
    private val _onDeleteClick: (Order) -> Unit,
    private val _onEditClick: (Order) -> Unit,
    private val _onChangeDistributorClick: ((Order) -> Unit)? = null,
    private val _onItemClick: ((Order) -> Unit)? = null
) {

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

    data class Item(
        val ID: Int,
        val orderID: Int,
        val beerID: Int,
        val beer: BeerModel,
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
        ): TempBeerItemModel {
            val canType = barrels.find { it.id == canTypeID }
            return TempBeerItemModel(
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

    data class Sales(
        val orderID: Int,
        val beerID: Int,
        val beer: BeerModel,
        val check: Int,
        val canTypeID: Int,
        val count: Int
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
}

enum class OrderStatus(val textRes: Int, val data: Int) {
    @Json(name = "order_active")
    ACTIVE(R.string.active, 1),

    @Json(name = "order_completed")
    COMPLETED(R.string.completed, 2),

    @Json(name = "order_canceled")
    CANCELED(R.string.canceled, 3),

    @Json(name = "order_deleted")
    DELETED(R.string.deleted, 4),

    @Json(name = "order_auto_created")
    AUTO_CREATED(R.string.auto_created, 5)
}