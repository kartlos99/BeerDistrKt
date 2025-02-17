package com.example.beerdistrkt.fragPages.expense.data

import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseDto
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import javax.inject.Inject

class ExpenseDtoMapper @Inject constructor() {

    fun mapToDto(expense: Expense): ExpenseDto =
        ExpenseDto(
            id = expense.id,
            distributorID = expense.distributor.id,
            amount = expense.amount,
            comment = expense.comment,
            date = expense.date,
            category = expense.category.id ?: -1
        )
}