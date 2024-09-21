package com.example.beerdistrkt.fragPages.realisationtotal.domain.model

import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.models.BarrelIO
import com.example.beerdistrkt.models.MoneyInfo
import com.example.beerdistrkt.models.SaleInfo

data class RealizationDay(
    val sale: List<SaleInfo>,
    val takenMoney: List<MoneyInfo>,
    val barrels: List<BarrelIO>,
    val expenses: List<Expense>,
    val bottleSale: List<BottleSale>,
) {
    fun getTotalPrice(): Double = sale.sumOf { it.price } + bottleSale.sumOf { it.price }

    fun getCashAmount(): Double = takenMoney
        .filter { it.paymentType == PaymentType.Cash }
        .sumOf { it.amount }

    fun getTransferAmount(): Double = takenMoney
        .filter { it.paymentType == PaymentType.Transfer }
        .sumOf { it.amount }

    fun getTotalExpense() = expenses.sumOf { it.amount }
}
