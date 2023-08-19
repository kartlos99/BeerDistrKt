package com.example.beerdistrkt.fragPages.reporting

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.reporting.repo.ChangesRepository

class ChangesListViewModel : BaseViewModel() {

    val changesRepository = ChangesRepository()

    init {
        getChanges()
    }

    private fun getChanges() {

    }
}