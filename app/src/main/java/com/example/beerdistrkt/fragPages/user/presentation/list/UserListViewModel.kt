package com.example.beerdistrkt.fragPages.user.presentation.list

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUsersUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.RefreshUsersUseCase
import com.example.beerdistrkt.network.model.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
) : BaseViewModel() {

    val usersStateFlow: StateFlow<ResultState<List<User>>>
        get() = getUsersUseCase.usersAsFlow()

    fun refreshUsers() {
        viewModelScope.launch {
            refreshUsersUseCase()
        }
    }
}
