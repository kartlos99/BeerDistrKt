package com.example.beerdistrkt.common.domain.model

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import com.example.beerdistrkt.R
import com.squareup.moshi.Json

enum class EntityStatus(
    val value: String,
    @StringRes val displayName: Int,
) {
    @Json(name = "0")
    DELETED("0", R.string.deleted),

    @Json(name = "1")
    ACTIVE("1", R.string.active),

    @Json(name = "2")
    INACTIVE("2", R.string.not_active);

    companion object {
        fun fromValue(value: String): EntityStatus? {
            return entries.firstOrNull {
                it.value == value
            }
        }

        fun fromDisplayName(context: Context, displayName: String): EntityStatus? {
            return EntityStatus.entries.firstOrNull {
                context.getString(it.displayName) == displayName
            }
        }

        fun getDropDownAdapter(context: Context): ArrayAdapter<String> {
            val data = EntityStatus.entries
                .filter { it.value > "0" }
                .map { context.getString(it.displayName) }
            return ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                data
            )
        }
    }
}