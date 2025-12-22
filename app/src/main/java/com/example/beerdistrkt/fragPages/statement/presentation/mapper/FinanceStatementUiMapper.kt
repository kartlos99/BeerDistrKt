package com.example.beerdistrkt.fragPages.statement.presentation.mapper

import com.example.beerdistrkt.fragPages.statement.domain.model.FStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatementDetails
import com.example.beerdistrkt.fragPages.statement.presentation.model.FStatementUiItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.SaleItemUiModel
import javax.inject.Inject

class FinanceStatementUiMapper @Inject constructor() {

    fun map(item: FStatement): FStatementUiItem = when (item) {
        is FStatement.PayMoney -> FStatementUiItem.Money(
            dateStr = item.dateStr,
            comment = item.comment,
            pay = item.amount,
            balance = item.balance,
            recordId = item.recordId
        )

        is FStatement.SaleGroup -> FStatementUiItem.Sale(
            dateStr = item.dateStr,
            comment = item.comment,
            price = item.saleItems.sumOf { it.price },
            balance = item.balance,
            isGift = item.isGift,
            isSoldToday = item.isSoldToday,
            items = item.saleItems.map {
                SaleItemUiModel(
                    price = it.price,
                    recordId = it.recordId,
                    recordType = it.recordType,
                    itemColor = when (it.details) {
                        is FinanceStatementDetails.BeerDetails -> it.details.beer.displayColor
                        else -> null
                    },
                    details = it.details?.let(::mapDetails).orEmpty()
                )
            }
        )
    }

    fun mapDetails(details: FinanceStatementDetails): String {

        return when (details) {
            is FinanceStatementDetails.BeerDetails ->
                "${details.barrel.volume} x${details.count} (${details.beer.name.substring(0..2)})"

            is FinanceStatementDetails.BottleDetails ->
                "x${details.count} (${details.bottle.name.substring(0..2)})"
        }
    }
}