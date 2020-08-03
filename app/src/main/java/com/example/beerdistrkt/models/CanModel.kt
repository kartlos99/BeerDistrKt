package com.example.beerdistrkt.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "can_table")
data class CanModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    val volume: Int = 0,
    val sortValue: String
) {

}


