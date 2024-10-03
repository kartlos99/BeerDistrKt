package com.example.beerdistrkt.fragPages.statement.model

import android.annotation.SuppressLint
import com.example.beerdistrkt.fragPages.realisation.AddDeliveryFragment
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.Date

data class StatementModel(
    @Json(name = "dt")
    var tarigi: String = "",
    var comment: String? = "",

    var k_in: Int = 0,
    var k_out: Int = 0,

    @Json(name = "pr")
    var price: Float = 0f,
    var pay: Float = 0f,

    @Json(name = "bal")
    var balance: Float = 0f,

    var recordType: StatementRecordType = StatementRecordType.NONE,
    var id: Int = 0
) {
    @SuppressLint("SimpleDateFormat")
    fun getItemDate(pattern: String): Date? {
        return SimpleDateFormat(pattern).parse(tarigi)
    }

    fun getType(location: Int): String = when {
        location == 0 && pay != 0F -> AddDeliveryFragment.M_OUT
        location == 1 && k_out != 0 -> AddDeliveryFragment.K_OUT
        recordType == StatementRecordType.SALE_BOTTLE -> AddDeliveryFragment.MITANA_BOTTLE
        else -> AddDeliveryFragment.MITANA
    }

}