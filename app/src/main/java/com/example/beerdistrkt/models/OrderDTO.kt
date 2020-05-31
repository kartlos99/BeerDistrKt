package com.example.beerdistrkt.models

import com.squareup.moshi.Json

data class OrderDTO(
    val ID: Int,
    val orderDate: String,
    val orderStatusID: Int,
    val distributorID: Int,
    val clientID: Int,
    val comment: String?,
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
        fun toPm(): Order.Sales {
            return Order.Sales(
                orderID,
                beerID,
                check,
                canTypeID,
                count
            )
        }
    }

    fun toPm(clients: List<Obieqti>, beerList: List<BeerModel>): Order {

        val client = clients.find {
            it.id ?: 0 == clientID
        } ?: Obieqti.emptyModel

        return Order(
            ID,
            orderDate,
            orderStatusID,
            distributorID,
            clientID,
            client,
            comment,
            modifyDate,
            modifyUserID,
            items.map {
                it.toPm(beerList)
            },
            sales.map {
                it.toPm()
            }
        )

    }
}