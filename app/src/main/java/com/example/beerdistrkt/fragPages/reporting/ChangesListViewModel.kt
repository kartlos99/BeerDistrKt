package com.example.beerdistrkt.fragPages.reporting

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto
import com.example.beerdistrkt.fragPages.reporting.repo.ChangesRepository
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

class ChangesListViewModel : BaseViewModel() {

    private val changesRepository = ChangesRepository()

    val changesFlow = changesRepository.changesListFlow.asStateFlow()

    init {
        getChanges()
    }

    private fun getChanges() {
        changesRepository.getChangesList()
    }
}