package com.example.beerdistrkt.fragPages.homePage.domain

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.homePage.domain.model.CommentModel
import com.example.beerdistrkt.network.api.ApiResponse

interface HomeRepository {
    suspend fun getBarrels(): List<Barrel>
    suspend fun refreshBaseData()
    suspend fun getComments(): ApiResponse<List<CommentModel>>
}