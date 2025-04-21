package com.example.beerdistrkt.fragPages.bottle.domain.model

import android.content.Context
import androidx.annotation.StringRes
import com.example.beerdistrkt.R
import com.squareup.moshi.Json

enum class BottleStatus(@StringRes val displayName: Int) {

    @Json(name = "0")
    DELETED(R.string.bottle_status_title_deleted),

    @Json(name = "1")
    ACTIVE(R.string.bottle_status_title_active),

    @Json(name = "2")
    INACTIVE(R.string.bottle_status_title_not_active),

    @Json(name = "3")
    DELETED_BY_USER(R.string.bottle_status_title_deleted_by_user);

    companion object {
        fun from(context: Context, displayName: String): BottleStatus? {
            return entries.firstOrNull {
                context.getString(it.displayName) == displayName
            }
        }
    }
}