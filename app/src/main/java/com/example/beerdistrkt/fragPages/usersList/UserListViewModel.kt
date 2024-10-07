package com.example.beerdistrkt.fragPages.usersList

import com.example.beerdistrkt.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor() : BaseViewModel() {

    val usersLiveData = database.getActiveUsers()


}
