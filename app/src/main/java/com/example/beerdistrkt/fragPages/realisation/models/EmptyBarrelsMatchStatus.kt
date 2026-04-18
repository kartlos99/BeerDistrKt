package com.example.beerdistrkt.fragPages.realisation.models

import androidx.annotation.AttrRes
import com.example.beerdistrkt.R

enum class EmptyBarrelsMatchStatus(@AttrRes val colorAttrRes: Int) {
    MATCHED(R.attr.colorPayment),
    MISSED(R.attr.colorError),
    UNDEFINED(R.attr.colorWarning)
}