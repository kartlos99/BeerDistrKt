package com.example.beerdistrkt.fragPages.bottle.presentation

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BottleListViewModel @Inject constructor(
    private val getBottlesUseCase: GetBottlesUseCase,
) : BaseViewModel() {

    suspend fun getBottleList(): List<Bottle> = getBottlesUseCase()

}