package com.example.beerdistrkt.fragPages.statement.presentation.model

import android.text.Spannable
import com.example.beerdistrkt.utils.DiffItem

data class BarrelStatementUiModel(
    val dateStr: String,
    val comment: String?,
    val recordIds: List<Long>,
    val balance50: Int,
    val balance30: Int,
    val balance20: Int,
    val balance10: Int,
    val inOut50: Spannable,
    val inOut30: Spannable,
    val inOut20: Spannable,
    val inOut10: Spannable,
    val details: String? = null,
    val color: Int? = null,
) : DiffItem {

    var isExpanded: Boolean = false

    override val key: String
        get() = recordIds.joinToString(".")
}
