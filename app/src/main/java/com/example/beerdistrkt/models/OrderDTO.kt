package com.example.beerdistrkt.models

import com.squareup.moshi.Json

data class OrderDTO(
    val ID: Int,
    val orderDate: String,
    val orderStatusID: Int,
    val distributorID: Int,
    val clientID: Int,
    val comment: String?,
    val sortValue: Double,
    val modifyDate: String,
    val modifyUserID: Int,
    val items: List<Item>,
    val sales: List<Sales>
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
        fun toPm(beerList: List<BeerModel>): Order.Item {
            val beer = beerList.find { it.id == beerID } ?: BeerModel()
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
        fun toPm(beerList: List<BeerModel>): Order.Sales {
            val beer = beerList.find { it.id == beerID } ?: BeerModel()
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

    fun toPm(
        clients: List<Obieqti>,
        beerList: List<BeerModel>,
        onDeleteClick: (Order) -> Unit,
        onEditClick: (Order) -> Unit,
        onChangeDistributorClick: ((Order) -> Unit)? = null
    ): Order {

        val client = clients.find {
            it.id ?: 0 == clientID
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
            sortValue,
            modifyDate,
            modifyUserID,
            items.map {
                it.toPm(beerList)
            },
            sales.map {
                it.toPm(beerList)
            },
            onDeleteClick,
            onEditClick,
            onChangeDistributorClick
            )

    }
}