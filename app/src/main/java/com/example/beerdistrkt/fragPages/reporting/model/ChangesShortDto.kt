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
    val displayName: String,
    val headerItems: List<String>,
    val visibleKeys: List<String>
) {
    @Json(name = "barrel_output")
    BarrelOutput(
        tableName = "barrel_output",
        displayName = "კასრის დაბრუნება",
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
        displayName = "ობიექტი/მაღაზია",
        headerItems = listOf(
            "დასახელება",
            "მისამართი",
            "ტელ.ნომერი",
            "ს/კ",
            "საკონტაქტო პირი",
            "მონიშვნა",
            "კომენტარი"
        ),
        visibleKeys = listOf("dasaxeleba", "adress", "tel", "sk", "sakpiri", "isChecked", "comment")
    ),

    @Json(name = "moneyoutput")
    MoneyOutput(
        tableName = "moneyoutput",
        displayName = "თანხის აღება",
        headerItems = listOf(
            "თარიღი",
            "ობიექტი",
            "დისტრიბუტორი",
            "თანხა",
            "გადახდის ტიპი",
            "კომენტარი"
        ),
        visibleKeys = listOf("tarigi", "clientID", "distributorID", "tanxa", "paymentType", "comment")
    )
}