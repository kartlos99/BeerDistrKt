package com.example.beerdistrkt.fragPages.statement.presentation.mapper

import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatementDetails
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatementItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.FinanceStatementUiModel
import javax.inject.Inject

class FinanceStatementUiMapper @Inject constructor() {

    fun map(item: FinanceStatementItem): FinanceStatementUiModel {
        return FinanceStatementUiModel(
            dateStr = item.dateStr,
            price = item.price,
            pay = item.pay,
            balance = item.balance,
            recordType = item.recordType,
            details = item.details?.let(::mapDetails),
            comment = item.comment,
            recordId = item.recId,
            iconRes = if (item.isGift()) R.drawable.ic_gift_24 else item.recordType.icon,
            color = when (item.details) {
                is FinanceStatementDetails.BeerDetails -> item.details.beer.displayColor
                else -> null
            },
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