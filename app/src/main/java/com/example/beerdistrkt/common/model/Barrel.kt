package com.example.beerdistrkt.common.model

import com.example.beerdistrkt.common.domain.model.EntityStatus

data class Barrel(
    val id: Int,
    val name: String,
    val volume: Int,
    val status: EntityStatus,
    val sortValue: String,
    val image: String?,
)

enum class BarrelEnum(
    val id: Int,
    val displayName: String,
    val volume: Int,
    val status: EntityStatus,
    val sortValue: String,
    val image: String?,
) {
    BARREL_50(1, BARREL_50_NAME, 50, EntityStatus.ACTIVE, "10", null),
    BARREL_30(2, BARREL_30_NAME, 30, EntityStatus.ACTIVE, "20", null),
    BARREL_20(3, BARREL_20_NAME, 20, EntityStatus.ACTIVE, "30", null),
    BARREL_10(4, BARREL_10_NAME, 10, EntityStatus.ACTIVE, "40", null);

    companion object  {
        fun findById(id: Int): BarrelEnum? = entries.firstOrNull { it.id == id }
    }
}

private const val BARREL_50_NAME = "50იანი"
private const val BARREL_30_NAME = "30იანი"
private const val BARREL_20_NAME = "20იანი"
private const val BARREL_10_NAME = "10იანი"