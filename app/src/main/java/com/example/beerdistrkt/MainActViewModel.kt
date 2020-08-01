package com.example.beerdistrkt

import com.example.beerdistrkt.models.ChangePassRequestModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.Session

class MainActViewModel: BaseViewModel() {

    fun changePassword(oldPass: String, newPass: String, callback: (text: String?) -> Unit) {
        sendRequest(
            ApeniApiService.getInstance().changePassword(
                ChangePassRequestModel(
                    Session.get().userID ?: "",
                    oldPass,
                    newPass
                )
            ),
            success = {
                callback(null)
            },
            responseFailure = {_, error ->
                callback(error)
            }
        )
    }

}