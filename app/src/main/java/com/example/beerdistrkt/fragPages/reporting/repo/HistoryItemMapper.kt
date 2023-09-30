package com.example.beerdistrkt.fragPages.reporting.repo

import com.example.beerdistrkt.fragPages.reporting.model.BarrelOutputRecord
import com.example.beerdistrkt.fragPages.reporting.model.BaseTableRecord
import com.example.beerdistrkt.fragPages.reporting.model.CustomerRecord
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class HistoryItemMapper {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val customerRecordAdapter: JsonAdapter<CustomerRecord> =
        moshi.adapter(CustomerRecord::class.java)
    private val barrelOutputRecordAdapter: JsonAdapter<BarrelOutputRecord> =
        moshi.adapter(BarrelOutputRecord::class.java)

    fun map(data: String, tableName: DbTableName): BaseTableRecord? {
        val readyObject = when (tableName) {
            DbTableName.BarrelOutput -> barrelOutputRecordAdapter.fromJson(data)
            DbTableName.Customer -> customerRecordAdapter.fromJson(data)
        }
        return readyObject
    }
}