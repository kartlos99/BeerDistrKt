package com.example.beerdistrkt.fragPages.expense.domain.model

import android.graphics.Color
import android.os.Parcelable
import com.example.beerdistrkt.common.domain.model.EntityStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpenseCategory(
    val id: Int? = null,
    val name: String,
    val status: EntityStatus,
    val color: Int,
) : Parcelable {

    companion object {
        fun newInstance() = ExpenseCategory(
            name = "",
            status = EntityStatus.ACTIVE,
            color = Color.GRAY
        )
    }
}