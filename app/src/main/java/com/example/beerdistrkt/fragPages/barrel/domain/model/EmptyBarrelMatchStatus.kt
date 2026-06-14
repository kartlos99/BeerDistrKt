package com.example.beerdistrkt.fragPages.barrel.domain.model

import android.graphics.Color

enum class EmptyBarrelMatchStatus(val color: Int) {
    MATCH(Color.GREEN),
    LESS(Color.RED),
    MORE(Color.YELLOW),
}