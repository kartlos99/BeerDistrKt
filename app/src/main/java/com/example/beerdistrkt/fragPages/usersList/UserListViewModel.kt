package com.example.beerdistrkt.fragPages.usersList

import com.example.beerdistrkt.BaseViewModel

class UserListViewModel : BaseViewModel() {

    val usersLiveData = database.getActiveUsers()


}
