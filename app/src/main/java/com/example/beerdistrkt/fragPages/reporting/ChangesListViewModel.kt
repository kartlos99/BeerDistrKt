package com.example.beerdistrkt.fragPages.reporting

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.reporting.repo.ChangesRepository
import kotlinx.coroutines.flow.asStateFlow

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