package com.example.beerdistrkt.fragPages.addEditObiects.model

import android.content.Context
import androidx.annotation.StringRes
import com.example.beerdistrkt.R
import com.squareup.moshi.Json

enum class CustomerGroup(@StringRes val displayName: Int) {
    @Json(name = "B")
    BASE(R.string.customer_base),

    @Json(name = "R")
    RESTAURANT(R.string.customer_restaurant);

    companion object {
        fun from(context: Context, displayName: String): CustomerGroup {
            return entries.firstOrNull {
                context.getString(it.displayName) == displayName
            } ?: BASE
        }
    }
}