package com.example.beerdistrkt.fragPages.bottlemanagement

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase.GetBottleUseCase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BottleListViewModel @Inject constructor(
    private val getBottleUseCase: GetBottleUseCase,
) : BaseViewModel() {

    suspend fun getBottleList(): List<BaseBottleModel> = getBottleUseCase()

}