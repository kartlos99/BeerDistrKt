package com.example.beerdistrkt.fragPages.reporting.model

import com.example.beerdistrkt.utils.DiffItem
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
): DiffItem {
    override val key: Int
        get() = id
}


enum class DbTableName(val tableName: String) {
    @Json(name = "barrel_output")
    BarrelOutput("barrel_output"),

    @Json(name = "customer")
    Customer("customer")
}