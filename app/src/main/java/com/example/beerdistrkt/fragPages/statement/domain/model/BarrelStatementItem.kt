package com.example.beerdistrkt.fragPages.statement.domain.model

import com.example.beerdistrkt.common.model.BarrelEnum

data class BarrelStatementItem(
    val dateStr: String,
    val balance: BarrelsBalance,
    val ioItems: List<BarrelIo>,
    val comment: String?,
)

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