package com.example.beerdistrkt.fragPages.login.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.login.domain.usecase.SignInUseCase
import com.example.beerdistrkt.network.api.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : BaseViewModel() {

    private val _loginDataStateFlow: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val loginDataStateFlow: StateFlow<LoginState> = _loginDataStateFlow.asStateFlow()

    private val _openHomeScreenFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val openHomeScreenFlow: SharedFlow<Unit> = _openHomeScreenFlow.asSharedFlow()

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            _loginDataStateFlow.update {
                it.copy(
                    isLoading = true,
                    errorMessageRes = null,
                )
            }
            when (val result = signInUseCase(username, password)) {
                is ApiResponse.Error -> handleError(result.errorCode)
                is ApiResponse.Success<*> -> _openHomeScreenFlow.emit(Unit)
            }
        }
    }

    private suspend fun handleError(errorCode: String?) {
        val errorMsgRes = when (errorCode) {
            ERROR_CODE_CANT_IDENTIFY_USER -> R.string.error_cant_identify_user
            ERROR_CODE_INCORRECT_PASSWORD -> R.string.error_incorrect_password
            ERROR_CODE_NO_REGION_ATTACHED -> R.string.error_no_region_attached
            else -> R.string.error_unknown
        }
        _loginDataStateFlow.emit(
            LoginState(
                isLoading = false,
                isLoginEnabled = true,
                errorMessageRes = errorMsgRes
            )
        )
    }

    companion object {
        const val ERROR_CODE_CANT_IDENTIFY_USER = "1525"
        const val ERROR_CODE_INCORRECT_PASSWORD = "1526"
        const val ERROR_CODE_NO_REGION_ATTACHED = "1527"
    }

    data class LoginState(
        val isLoading: Boolean = false,
        val isLoginEnabled: Boolean = true,
        val errorMessageRes: Int? = null,
    )
}
