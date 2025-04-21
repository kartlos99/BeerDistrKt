package com.example.beerdistrkt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.fragPages.customer.domain.usecase.RefreshCustomersUseCase
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.RefreshBaseDataUseCase
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.fragPages.settings.domain.usecase.RefreshSettingsUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.RefreshUsersUseCase
import com.example.beerdistrkt.models.ChangePassRequestModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActViewModel @Inject constructor(
    private val refreshBaseDataUseCase: RefreshBaseDataUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val refreshCustomers: RefreshCustomersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val refreshSettingsUseCase: RefreshSettingsUseCase,
    override var session: Session
) : BaseViewModel() {

    val headerUpdateLiveData = MutableLiveData<Int>()

    val showContentFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _eventsFlow: MutableSharedFlow<ActUiEvent> = MutableSharedFlow()
    val eventsFlow: SharedFlow<ActUiEvent> = _eventsFlow.asSharedFlow()

    init {
        updateInitialData()
    }

    private fun updateInitialData() = viewModelScope.launch {
        showContentFlow.emit(false)
        userPreferencesRepository.readUserSession().also { userInfo ->
            session.restoreFromSavedInfo(userInfo)
        }
        userPreferencesRepository.readRegion().also { region ->
            session.restoreLastRegion(region)
            region?.let {
                refreshCustomers()
                refreshUsersUseCase()
            }
        }
        refreshBaseDataUseCase()
        refreshSettingsUseCase()
        showContentFlow.emit(true)
    }

    fun changePassword(oldPass: String, newPass: String, callback: (text: String?) -> Unit) {
        sendRequest(
            ApeniApiService.getInstance().changePassword(
                ChangePassRequestModel(
                    session.userID.orEmpty(),
                    oldPass,
                    newPass
                )
            ),
            success = {
                callback(null)
            },
            responseFailure = { _, error ->
                callback(error)
            }
        )
    }

    fun updateNavHeader() {
        headerUpdateLiveData.value = 1
    }

    fun performUserLogout() {
        viewModelScope.launch {
            session.clearSession()
            session.clearUserPreference()
            session.loggedIn = false
            SharedPreferenceDataSource.getInstance().saveUserName("")
            SharedPreferenceDataSource.getInstance().savePassword("")
            _eventsFlow.emit(ActUiEvent.GoToLoginPage)
        }
    }

    sealed interface ActUiEvent {
        data object GoToLoginPage: ActUiEvent
    }
}