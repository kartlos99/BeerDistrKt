package com.example.beerdistrkt.fragPages.realisationtotal.domain.model

import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.models.BarrelIO
import com.example.beerdistrkt.models.MoneyInfo
import com.example.beerdistrkt.models.SaleInfo

data class RealizationDay(
    val sale: List<SaleInfo>,
    val takenMoney: List<MoneyInfo>,
    val barrels: List<BarrelIO>,
    val expenses: List<Expense>,
    val bottleSale: List<BottleSale>,
)
