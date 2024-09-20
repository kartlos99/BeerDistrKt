package com.example.beerdistrkt.fragPages.realisationtotal.data.model

import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseDto
import com.example.beerdistrkt.models.BarrelIO
import com.example.beerdistrkt.models.MoneyInfo
import com.example.beerdistrkt.models.SaleInfo

data class RealizationDayDto(
    val sale: List<SaleInfo>,
    val takenMoney: List<MoneyInfo>,
    val barrels: List<BarrelIO>,
    val expenses: List<ExpenseDto>,
    val bottleSale: List<BottleSaleDto>,
)
