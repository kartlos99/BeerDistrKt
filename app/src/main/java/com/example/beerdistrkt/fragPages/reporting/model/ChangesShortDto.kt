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
) : DiffItem {
    override val key: Int
        get() = id
}


enum class DbTableName(
    val tableName: String,
    val columnCount: Int,
    val headerItems: List<String>,
    val visibleKeys: List<String>
) {
    @Json(name = "barrel_output")
    BarrelOutput(
        tableName = "barrel_output",
        columnCount = 6,
        headerItems = listOf(
            "ობიექტი",
            "დაბრუნების თარიღი",
            "დისტრიბუტორი",
            "კასრის ტიპი",
            "რაოდენობა",
            "კომენტარი"
        ),
        visibleKeys = listOf(
            "clientID",
            "outputDate",
            "distributorID",
            "canTypeID",
            "count",
            "comment"
        )
    ),

    @Json(name = "customer")
    Customer(
        tableName = "customer",
        columnCount = 7,
        headerItems = listOf(
            "დასახელება",
            "მისამართი",
            "ტელ.ნომერი",
            "ს/კ",
            "საკონტაქტო პირი",
            "მონიშვნა",
            "კომენტარი"
        ),
        visibleKeys = listOf("dasaxeleba", "adress", "tel", "sk", "sakpiri", "chek", "comment")
    )
}