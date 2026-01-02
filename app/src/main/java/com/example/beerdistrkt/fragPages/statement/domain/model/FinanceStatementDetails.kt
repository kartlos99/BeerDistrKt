package com.example.beerdistrkt.fragPages.statement.domain.model

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle

sealed class FinanceStatementDetails {

    data class BeerDetails(
        val count: Int,
        val beer: Beer,
        val barrel: Barrel,
    ) : FinanceStatementDetails()

    data class BottleDetails(
        val count: Int,
        val bottle: Bottle,
    ) : FinanceStatementDetails()
}

fun FinanceStatementDetails.getProductName(): String = when (this) {
    is FinanceStatementDetails.BeerDetails -> beer.name
    is FinanceStatementDetails.BottleDetails -> bottle.name
}