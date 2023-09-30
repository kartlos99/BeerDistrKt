package com.example.beerdistrkt.fragPages.reporting.model

import com.squareup.moshi.Json

abstract class BaseTableRecord {
    abstract val hid: String
    abstract val id: String
    abstract val modifyDate: String
    abstract val modifyUserID: Int
}

data class CustomerRecord(
    @Json(name = "dasaxeleba")
    val name: String,
    @Json(name = "adress")
    val address: String,
    val tel: String,
    val comment: String?,
    @Json(name = "sk")
    val identificationNumber: String,
    @Json(name = "sakpiri")
    val contact: String,
    @Json(name = "chek")
    val check: Int,

    override val hid: String,
    override val id: String,
    override val modifyDate: String,
    override val modifyUserID: Int,
) : BaseTableRecord()

data class BarrelOutputRecord(
    val outputDate: String,
    val clientID: Int,
    val distributorID: Int,
    val canTypeID: Int,
    val count: Int,
    val comment: String?,

    override val hid: String,
    override val id: String,
    override val modifyDate: String,
    override val modifyUserID: Int,
) : BaseTableRecord()