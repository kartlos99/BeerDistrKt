package com.example.beerdistrkt.fragPages.sawyobi.domain

import com.example.beerdistrkt.fragPages.sawyobi.data.StorehousePagingSource
import com.example.beerdistrkt.network.api.StorehouseIoRepository
import javax.inject.Inject

class GetStorehouseIoPagingSourceUseCase @Inject constructor(
    private val repo: StorehouseIoRepository
) {
    operator fun invoke() = StorehousePagingSource(repo)
}