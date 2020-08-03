package com.example.beerdistrkt.fragPages.orders.models

import com.example.beerdistrkt.fragPages.orders.adapter.OrderAdapter
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus

data class OrderGroupModel(
    val distributorID: Int,
    val distributorName: String,
    val ordersList: MutableList<Order>,
    var isExpanded: Boolean = true

) {

    var orderAdapter: OrderAdapter? = null

    fun getSummedSales(): List<Order.Sales> {
        val sumSaleItems = mutableListOf<Order.Sales>()
        val resultList = mutableListOf<Order.Sales>()
        ordersList
            .filter {
                it.orderStatus == OrderStatus.ACTIVE
            }
            .forEach {
            sumSaleItems.addAll(it.sales)
        }
        sumSaleItems.groupBy {
            it.beerID
        }.values.forEach { saleItemsList ->
            saleItemsList.groupBy { it.canTypeID }.values.forEach { singleSale ->
                val count = singleSale.sumBy { it.count }
                resultList.add(
                    Order.Sales(
                        0,
                        saleItemsList[0].beerID,
                        saleItemsList[0].beer,
                        0,
                        singleSale[0].canTypeID,
                        count
                    )
                )
            }
        }
        return resultList
    }
}