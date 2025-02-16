package com.example.beerdistrkt.fragPages.showHistory

import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.squareup.moshi.Json

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
        clients: List<Customer>,
        usersList: List<User>
    ): OrderHistory? {

        val client = clients.find {
            (it.id ?: 0) == clientID
        } ?: return null

        val distributor = usersList.find { it.id == distributorID.toString() } ?: return null
        val modifyUser = usersList.find { it.id == modifyUserID.toString() } ?: return null

        return OrderHistory(
            hID,
            ID,
            orderDate.split(" ")[0],
            orderStatus,
            distributor.username,
            client.name,
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
        clients: List<Customer>,
        usersList: List<User>,
        beerList: List<Beer>
    ): SaleHistory? {
        val client = clients.find {
            it.id == clientID
        } ?: return null
        val distributor = usersList.find { it.id == distributorID.toString() } ?: return null
        val modifyUser = usersList.find { it.id == modifyUserID.toString() } ?: return null
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
    val client: Customer,
    val distributor: User,
    val beer: Beer,
    val chek: Int,
    val unitPrice: Double,
    val canTypeID: Int,
    val count: Int,
    val comment: String?,
    val modifyDate: String,
    val modifyUser: User
)

data class MoneyHistoryDTO(
    @Json(name = "tarigi")
    val operationDate: String,
    @Json(name = "obieqtis_id")
    val clientID: Int,
    @Json(name = "distributor_id")
    val distributorID: Int,
    @Json(name = "tanxa")
    val moneyAmount: Double,
    val paymentType: PaymentType,
    val comment: String?,
    val modifyDate: String,
    val modifyUserID: Int
) {
    fun toPm(
        clients: List<Customer>,
        usersList: List<User>
    ): MoneyHistory? {
        val client = clients.find {
            it.id == clientID
        } ?: return null
        val distributor = usersList.find { it.id == distributorID.toString() } ?: return null
        val modifyUser = usersList.find { it.id == modifyUserID.toString() } ?: return null

        return MoneyHistory(
            operationDate,
            client,
            distributor,
            moneyAmount,
            paymentType,
            comment,
            modifyDate,
            modifyUser
        )
    }
}

data class MoneyHistory(
    val operationDate: String,
    val client: Customer,
    val distributor: User,
    val moneyAmount: Double,
    val paymentType: PaymentType,
    val comment: String?,
    val modifyDate: String,
    val modifyUser: User,
)


data class BottleSaleHistoryDTO(
    val saleDate: String,
    val clientID: Int,
    val distributorID: Int,
    val bottleID: Int,
    val price: Double,
    val count: Int,
    val comment: String?,
    val modifyDate: String,
    val modifyUserID: Int
) {
    fun toPm(
        clients: List<Customer>,
        usersList: List<User>,
        bottles: List<BaseBottleModel>
    ): BottleSaleHistory? {
        val client = clients.find {
            it.id == clientID
        } ?: return null
        val distributor = usersList.find { it.id == distributorID.toString() } ?: return null
        val modifyUser = usersList.find { it.id == modifyUserID.toString() } ?: return null
        val bottle = bottles.find { it.id == bottleID } ?: return null

        return BottleSaleHistory(
            saleDate,
            client,
            distributor,
            bottle,
            price,
            count,
            comment,
            modifyDate,
            modifyUser
        )
    }
}

data class BottleSaleHistory(
    val saleDate: String,
    val client: Customer,
    val distributor: User,
    val bottle: BaseBottleModel,
    val unitPrice: Double,
    val count: Int,
    val comment: String?,
    val modifyDate: String,
    val modifyUser: User,
)