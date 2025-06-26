package com.example.beerdistrkt.fragPages.login.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.domain.usecase.CheckAuthUseCase
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoaderScreenViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val checkAuthUseCase: CheckAuthUseCase,
    override var session: Session,
) : BaseViewModel() {

    private val _actionFlow: MutableSharedFlow<Action> = MutableSharedFlow(1, 1)
    val openHomeScreenFlow: SharedFlow<Action> = _actionFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            readSavedData()
        }
    }

    private suspend fun readSavedData() {
        userPreferencesRepository.readUserSession().also { userInfo ->
            session.restoreFromSavedInfo(userInfo)
//            if (session.isAccessTokenValid())
//                refreshSettingsUseCase()
        }
        userPreferencesRepository.readRegion().also { region ->
            session.restoreLastRegion(region)
//            region?.let {
//                refreshCustomers()
//                refreshUsersUseCase()
//            }
        }

        if (session.isAccessTokenValid())
            makeAuthTestCall()
        else
            goToAuth()

    }

    private suspend fun makeAuthTestCall() {
        when (val result = checkAuthUseCase()) {
            is ApiResponse.Error -> goToAuth()
            is ApiResponse.Success -> _actionFlow.emit(Action.OpenHomePage)
        }
    }

    private suspend fun goToAuth() {
        session.clearSession()
        session.clearUserPreference()
        _actionFlow.emit(Action.OpenLoginPage)
    }

    sealed interface Action {
        data object OpenHomePage : Action
        data object OpenLoginPage : Action
    }
}