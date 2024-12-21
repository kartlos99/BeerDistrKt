package com.example.beerdistrkt.fragPages.homePage.domain

import com.example.beerdistrkt.common.model.Barrel

interface HomeRepository {
    suspend fun getBarrels(): List<Barrel>
    suspend fun refreshBaseData()
}