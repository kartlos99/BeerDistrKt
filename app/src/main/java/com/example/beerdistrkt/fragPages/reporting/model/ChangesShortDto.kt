package com.example.beerdistrkt.fragPages.reporting.model

import com.squareup.moshi.Json

data class ChangesShortDto(
    @Json(name = "ID")
    val id: Int,
    val tableName: DbTableName,
    val editedRecordID: Int,
    val modifyDate: String,
    val modifyUserID: Int,
    val modifyUsername: String,
    val shortInfo: Map<String, String>,
)


enum class DbTableName {
    @Json(name = "barrel_output")
    BarrelOutput,

    @Json(name = "customer")
    Customer
}