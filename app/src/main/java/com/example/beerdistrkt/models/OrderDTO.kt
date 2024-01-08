package com.example.beerdistrkt.models

import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.squareup.moshi.Json

data class OrderDTO(
    val ID: Int,
    val orderDate: String,
    val orderStatusID: Int,
    val distributorID: Int,
    val clientID: Int,
    val comment: String?,
    val isEdited: Int,
    val sortValue: Double,
    val modifyDate: String,
    val modifyUserID: Int,
    val needCleaning: Int,
    val passDays: Int,
    val items: List<Item>,
    val bottleItems: List<BottleItem>,
    val sales: List<Sales>,
    val bottleSales: List<BottleSaleItem>,
    val availableRegions: List<Int>
) {
    data class Item(
        val ID: Int,
        val orderID: Int,
        val beerID: Int,
        val canTypeID: Int,
        val count: Int,
        @Json(name = "chek")
        val check: Int,
        val modifyDate: String,
        val modifyUserID: Int
    ) {
        fun toPm(beerList: List<BeerModelBase>): Order.Item {
            val beer = beerList.find { it.id == beerID } ?: BeerModelBase()
            return Order.Item(
                ID,
                orderID,
                beerID,
                beer,
                canTypeID,
                count,
                check,
                modifyDate,
                modifyUserID
            )
        }
    }

    data class Sales(
        val orderID: Int,
        val beerID: Int,
        @Json(name = "chek")
        val check: Int,
        val canTypeID: Int,
        val count: Int
    ) {
        fun toPm(beerList: List<BeerModelBase>): Order.Sales {
            val beer = beerList.find { it.id == beerID } ?: BeerModelBase()
            return Order.Sales(
                orderID,
                beerID,
                beer,
                check,
                canTypeID,
                count
            )
        }
    }

    data class BottleItem(
        val id: Int,
        val orderID: Int,
        val bottleID: Int,
        val count: Int,
    ) {
        fun toPm(bottles: List<BaseBottleModel>): Order.BottleItem {
            val bottle = bottles.firstOrNull { it.id == bottleID }
                ?: throw NoSuchElementException("OrderDTO: can't match bottle for order item!")
            return Order.BottleItem(
                id,
                orderID,
                bottle,
                count
            )
        }
    }

    data class BottleSaleItem(
        val orderID: Int,
        val bottleID: Int,
        val count: Int,
    ) {
        fun toPm(bottles: List<BaseBottleModel>): Order.BottleSaleItem {
            val bottle = bottles.firstOrNull { it.id == bottleID }
                ?: throw NoSuchElementException("OrderDTO: can't match bottle for sale item!")
            return Order.BottleSaleItem(
                orderID,
                bottle,
                count
            )
        }
    }

    fun toPm(
        clients: List<Obieqti>,
        beerList: List<BeerModelBase>,
        bottles: List<BaseBottleModel>,
        onDeleteClick: (Order) -> Unit,
        onEditClick: (Order) -> Unit,
        onChangeDistributorClick: ((Order) -> Unit)? = null,
        onItemClick: ((Order) -> Unit)? = null,
        onHistoryClick: ((String) -> Unit)? = null
    ): Order {

        val client = clients.find {
            (it.id ?: 0) == clientID
        } ?: Obieqti.emptyModel

        return Order(
            ID,
            orderDate,
            when (orderStatusID) {
                1 -> OrderStatus.ACTIVE
                3 -> OrderStatus.CANCELED
                4 -> OrderStatus.DELETED
                5 -> OrderStatus.AUTO_CREATED
                else -> OrderStatus.COMPLETED
            },
            distributorID,
            clientID,
            client,
            comment,
            isEdited,
            sortValue,
            modifyDate,
            modifyUserID,
            needCleaning,
            passDays,
            items.map {
                it.toPm(beerList)
            },
            bottleItems.map {
                it.toPm(bottles)
            },
            sales.map {
                it.toPm(beerList)
            },
            bottleSales.map {
                it.toPm(bottles)
            },
            onDeleteClick,
            onEditClick,
            onChangeDistributorClick,
            onItemClick,
            onHistoryClick
        )

    }
}