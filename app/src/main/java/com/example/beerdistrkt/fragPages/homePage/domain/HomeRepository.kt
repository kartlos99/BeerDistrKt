package com.example.beerdistrkt.fragPages.homePage.domain

interface HomeRepository {
    suspend fun refreshBaseData()
}