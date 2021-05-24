package com.example.beerdistrkt.fragPages.addEditUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.addEditUser.models.AddUserRequestModel
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.models.AttachRegionsRequest
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.models.UserAttachRegionsRequest
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.launch

class AddUserViewModel(private val userID: String) : BaseViewModel() {

    private val _addUserLiveData = MutableLiveData<ApiResponseState<String>>()
    val addUserLiveData: LiveData<ApiResponseState<String>>
        get() = _addUserLiveData

    val deleteUserLiveData = MutableLiveData<ApiResponseState<String>>()

    val usersLiveData = database.getUsers()

    val userLiveData = MutableLiveData<User?>()
    var user: User? = null

    val userRegionsLiveData = MutableLiveData<ApiResponseState<List<AttachedRegion>>>()
    val regions = mutableListOf<AttachedRegion>()
    val selectedRegions = mutableListOf<AttachedRegion>()

    init {
        if (userID.isNotEmpty()) {
            usersLiveData.observeForever {
                user = it.find { u ->
                    u.id == userID
                }
                userLiveData.value = user
            }
        }
    }

    fun onDoneClick(model: AddUserRequestModel) {
        _addUserLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addUpdateUser(model),
            successWithData = {
                _addUserLiveData.value = ApiResponseState.Success(it)
                saveLocally(model.user.copy(id = it))
            },
            finally = {
                _addUserLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun saveLocally(user: User) {
        ioScope.launch {
            database.insertUser(user)
        }
    }

    fun deleteUser() {
        if (userID.isNotEmpty())
            sendRequest(
                ApeniApiService.getInstance().deleteRecord(
                    DeleteRequest(
                        userID, "users", Session.get().userID ?: ""
                    )
                ),
                success = {
                    ioScope.launch {
                        database.deleteUser(userID)
                    }
                    deleteUserLiveData.value = ApiResponseState.Success("")
                }
            )
    }

    fun getRegionForUser() {
        sendRequest(
            ApeniApiService.getInstance().getAttachedRegions(userID),
            successWithData = {
                regions.clear()
                regions.addAll(it)
                userRegionsLiveData.value =
                    ApiResponseState.Success(regions.filter { r -> r.isAttached })
            }
        )
    }

    fun getAllRegionNames(): Array<String> {
        return regions.map { it.name }.toTypedArray()
    }

    fun getSelectedRegions(): BooleanArray {
        selectedRegions.clear()
        selectedRegions.addAll(regions.filter { r -> r.isAttached })
        return regions.map { it.isAttached }.toBooleanArray()
    }

    fun setNewRegions() {
        val request = UserAttachRegionsRequest(
            userID,
            selectedRegions.map { it.ID }
        )
        sendRequest(
            ApeniApiService.getInstance().setRegions(request),
            successWithData = {
                getRegionForUser()
            }
        )
    }
}
