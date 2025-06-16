package com.example.beerdistrkt.fragPages.login.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoaderScreenViewModel @Inject constructor(

) : BaseViewModel() {

    private val _actionFlow: MutableSharedFlow<Action> = MutableSharedFlow()
    val openHomeScreenFlow: SharedFlow<Action> = _actionFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _actionFlow.emit(Action.OpenLoginPage)
        }
    }

    sealed interface Action {
        data object OpenHomePage : Action
        data object OpenLoginPage : Action
    }
}