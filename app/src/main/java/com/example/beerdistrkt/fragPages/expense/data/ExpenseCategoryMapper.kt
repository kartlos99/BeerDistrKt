package com.example.beerdistrkt.fragPages.expense.data

import android.graphics.Color
import com.example.beerdistrkt.asHexColor
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseCategoryDto
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import javax.inject.Inject

class ExpenseCategoryMapper @Inject constructor() {

    fun mapToDto(category: ExpenseCategory): ExpenseCategoryDto =
        ExpenseCategoryDto(
            id = category.id,
            name = category.name,
            status = category.status.value,
            color = category.color.asHexColor(),
        )

    fun mapToDomain(category: ExpenseCategoryDto): ExpenseCategory =
        ExpenseCategory(
            id = category.id,
            name = category.name,
            status = EntityStatus.fromValue(category.status) ?: EntityStatus.INACTIVE,
            color = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.GRAY
            },
        )
}