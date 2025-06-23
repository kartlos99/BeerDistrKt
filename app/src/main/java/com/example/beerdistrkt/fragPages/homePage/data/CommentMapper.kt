package com.example.beerdistrkt.fragPages.homePage.data

import com.example.beerdistrkt.fragPages.homePage.data.model.CommentDto
import com.example.beerdistrkt.fragPages.homePage.domain.model.CommentModel
import javax.inject.Inject


class CommentMapper @Inject constructor() {

    fun mapToDomain(dto: CommentDto): CommentModel = with(dto) {
        CommentModel(
            comment = comment,
            op = op,
            commentDate = commentDate,
            customerName = customerName,
            username = username
        )
    }
}