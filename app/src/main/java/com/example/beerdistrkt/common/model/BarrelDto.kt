package com.example.beerdistrkt.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.db.ApeniDatabaseDao

@Entity(tableName = ApeniDatabaseDao.BARREL_TB_NAME)
data class BarrelDto(
    @PrimaryKey
    val id: Int,
    val name: String,
    val volume: Int,
    val status: EntityStatus,
    val sortValue: String,
    val image: String?,
)
