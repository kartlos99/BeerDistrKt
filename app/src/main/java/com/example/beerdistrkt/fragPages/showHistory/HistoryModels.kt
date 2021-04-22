package com.example.beerdistrkt.fragPages.showHistory

import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.models.User

data class OrderHistoryDTO(
    val hID: Int,
    val ID: Int,
    val orderDate: String,
    val orderStatus: OrderStatus,
    val distributorID: Int,
    val clientID: Int,
    val comment: String?,
    val modifyDate: String,
    val modifyUserID: Int,
    val items: List<Item>? = null
) {
    data class Item(
        val hID: Int,
        val ID: Int,
        val orderID: Int,
        val beerID: Int,
        val canTypeID: Int,
        val count: Int,
        val chek: Int,
        val modifyDate: String,
        val modifyUserID: Int,
        val disrupterUserID: Int?
    )

    fun toOrderHistory(
        clients: List<Obieqti>,
        usersList: List<User>
    ): OrderHistory {

        val client = clients.find {
            it.id ?: 0 == clientID
        } ?: Obieqti.emptyModel

        val distributor = usersList.find { it.id == distributorID.toString() } ?: User.EMPTY_USER
        val modifyUser = usersList.find { it.id == modifyUserID.toString() } ?: User.EMPTY_USER

        return OrderHistory(
            hID,
            ID,
            orderDate.split(" ")[0],
            orderStatus,
            distributor.username,
            client.dasaxeleba,
            comment,
            modifyDate,
            modifyUser.username,
            items
        )
    }
}

data class OrderHistory(
    val hID: Int,
    val ID: Int,
    val orderDate: String,
    val orderStatus: OrderStatus,
    val distributor: String,
    val clientName: String,
    val comment: String?,
    val modifyDate: String,
    val modifyUser: String,
    val items: List<OrderHistoryDTO.Item>? = null
)

data class SaleHistoryDTO(
    val saleDate: String,
    val clientID: Int,
    val distributorID: Int,
    val beerID: Int,
    val chek: Int,
    val unitPrice: Double,
    val canTypeID: Int,
    val count: Int,
    val comment: String?,
    val modifyDate: String,
    val modifyUserID: Int
) {
    fun toPm(
        clients: List<Obieqti>,
        usersList: List<User>,
        beerList: List<BeerModel>
    ): SaleHistory? {
        val client = clients.find {
            it.id ?: 0 == clientID
        } ?: Obieqti.emptyModel
        val distributor = usersList.find { it.id == distributorID.toString() } ?: User.EMPTY_USER
        val modifyUser = usersList.find { it.id == modifyUserID.toString() } ?: User.EMPTY_USER
        val beer = beerList.find { it.id == beerID } ?: return null

        return SaleHistory(
            saleDate,
            client,
            distributor,
            beer,
            chek,
            unitPrice,
            canTypeID,
            count,
            comment,
            modifyDate,
            modifyUser
        )
    }
}

data class SaleHistory(
    val saleDate: String,
    val client: Obieqti,
    val distributor: User,
    val beer: BeerModel,
    val chek: Int,
    val unitPrice: Double,
    val canTypeID: Int,
    val count: Int,
    val comment: String?,
    val modifyDate: String,
    val modifyUser: User
)