package com.example.beerdistrkt.fragPages.sawyobi.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.DefaultApiClient

class StorehousePagingSource : PagingSource<Int, StorehouseIoDto>() {

    companion object {
        const val STARTING_KEY = 0
    }

    override fun getRefreshKey(state: PagingState<Int, StorehouseIoDto>): Int {
        return STARTING_KEY
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StorehouseIoDto> {

        val pageKey = params.key ?: STARTING_KEY

        return when (val apiResult = DefaultApiClient.getApi().getStorehouseIoPaged(pageKey)) {
            is ApiResponse.Error -> {
                LoadResult.Error(Exception(apiResult.message))
            }

            is ApiResponse.Success -> {
                LoadResult.Page(
                    data = apiResult.data,
                    prevKey = null,
                    nextKey = (pageKey + 1).takeIf { apiResult.data.isNotEmpty() }
                )
            }
        }

    }
}
