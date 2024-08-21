package com.example.beerdistrkt.fragPages.expense.domain.model

import android.os.Parcelable
import com.example.beerdistrkt.common.domain.model.EntityStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpenseCategory(
    val id: Int? = null,
    val name: String,
    val status: EntityStatus,
    val color: String,
): Parcelable
