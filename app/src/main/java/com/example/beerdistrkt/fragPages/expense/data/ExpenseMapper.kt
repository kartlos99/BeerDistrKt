package com.example.beerdistrkt.fragPages.expense.data

import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseDto
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import javax.inject.Inject

class ExpenseMapper @Inject constructor() {

    fun mapToDto(expense: Expense): ExpenseDto =
        ExpenseDto(
            id = expense.id,
            distributorID = expense.distributorID,
            amount = expense.amount,
            comment = expense.comment,
            date = expense.date,
            category = expense.category.id ?: -1
        )

    fun mapToDomain(
        expenseDto: ExpenseDto,
        categories: List<ExpenseCategory>
    ): Expense {
        return Expense(
            id = expenseDto.id,
            distributorID = expenseDto.distributorID,
            amount = expenseDto.amount,
            comment = expenseDto.comment,
            date = expenseDto.date,
            category = categories.firstOrNull {
                it.id == expenseDto.category
            } ?: throw NoSuchElementException(NO_MATCHING_CATEGORY)
        )
    }

    companion object {
        private const val NO_MATCHING_CATEGORY = "expense category can't be found!"
    }
}