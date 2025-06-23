package com.example.beerdistrkt.fragPages.homePage.domain.usecase

import com.example.beerdistrkt.fragPages.homePage.domain.HomeRepository
import com.example.beerdistrkt.fragPages.homePage.domain.model.CommentModel
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): ApiResponse<List<CommentModel>> {
        return homeRepository.getComments()
    }
}