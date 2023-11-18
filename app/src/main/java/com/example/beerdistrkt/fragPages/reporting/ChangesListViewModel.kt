package com.example.beerdistrkt.fragPages.reporting

import androidx.lifecycle.LiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto
import com.example.beerdistrkt.fragPages.reporting.repo.ChangesRepository
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.flow.MutableStateFlow

class ChangesListViewModel : BaseViewModel() {

    private val changesRepository = ChangesRepository()

    val changesLiveData: LiveData<ApiResponseState<List<ChangesShortDto>>> = changesRepository.changesListLiveData

    val changesListVisibilityState: MutableStateFlow<Boolean> = MutableStateFlow(true)

    init {
        getChanges()
    }

    private fun getChanges() {
        changesRepository.getChangesList()
    }

    fun toggleChangesList() {
        changesListVisibilityState.value = changesListVisibilityState.value.not()
    }
}