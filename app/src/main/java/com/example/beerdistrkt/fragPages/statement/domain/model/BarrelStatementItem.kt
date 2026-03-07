package com.example.beerdistrkt.fragPages.statement.domain.model

import com.example.beerdistrkt.common.model.BarrelEnum
import com.example.beerdistrkt.utils.DATETIME_PATTERN
import com.example.beerdistrkt.utils.daysBetween
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BarrelStatementItem(
    val dateStr: String,
    val balance: BarrelsBalance,
    val ioItems: List<BarrelIo>,
    val comment: String?,
) {
    val date: Date?
        get() = SimpleDateFormat(DATETIME_PATTERN, Locale.getDefault()).parse(dateStr)

    val isRegisteredToday: Boolean
        get() = date?.daysBetween() == 0L
}

data class BarrelIo(
    val countIn: Int,
    val countOut: Int,
    val barrel: BarrelEnum,
    val recId: Long,
)

data class BarrelsBalance(
    val balance50: Int,
    val balance30: Int,
    val balance20: Int,
    val balance10: Int,
)