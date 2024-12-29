package com.example.beerdistrkt.fragPages.addEditUser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.addEditUser.models.AddUserRequestModel
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUserUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.PutUserUseCase
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.models.UserAttachRegionsRequest
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.transform
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddUserViewModel.Factory::class)
class AddUserViewModel @AssistedInject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getUserUseCase: GetUserUseCase,
    private val putUserUseCase: PutUserUseCase,
    @Assisted private val userID: String,
) : BaseViewModel() {

    companion object {
        const val REGION_RESTRICTION_KAY: Int = 234
    }

    private var shouldSave: Boolean = false

    private val _apiState = MutableStateFlow<ResultState<Unit?>>(ResultState.Success(null))
    val apiState: StateFlow<ResultState<Unit?>> = _apiState.asStateFlow()

    val deleteUserLiveData = MutableLiveData<ApiResponseState<String>>()

    val userValidatorLiveData = MutableLiveData<UserValidationResult>()

    val userLiveData = MutableLiveData<User?>()

    val userRegionsLiveData = MutableLiveData<ApiResponseState<List<AttachedRegion>>>()
    val regions = mutableListOf<AttachedRegion>()
    val selectedRegions = mutableListOf<AttachedRegion>()

    init {
        if (userID.isNotEmpty()) {
            viewModelScope.launch {
                userLiveData.value = getUserUseCase(userID)
            }
        }
    }

    fun onDoneClick(
        userData: User,
        isChangingPassword: Boolean,
        password: String,
        confirmPassword: String
    ) {
        val requestModel = AddUserRequestModel(
            userData,
            password,
            isChangingPassword
        )
        val userValidatorResult =
            UserValidator(userData, isChangingPassword, password, confirmPassword).validate()
        userValidatorLiveData.value = userValidatorResult
        if (userValidatorResult is UserValidationResult.Success)
            putUser(requestModel)
    }

    private fun putUser(model: AddUserRequestModel) {
        viewModelScope.launch {
            _apiState.emit(ResultState.Loading)
            _apiState.emit(putUserUseCase(model).transform {})
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
            successWithData = { regions ->
                this.regions.clear()
                this.regions.addAll(regions)
                userRegionsLiveData.value =
                    ApiResponseState.Success(this.regions.filter { it.isAttached })

                if (Session.get().userID == userID) {
                    Session.get().regions.clear()
                    Session.get().regions.addAll(
                        regions
                            .filter { it.isAttached }
                            .map { it.toWorkRegion() }
                    )
                    if (shouldSave) {
                        shouldSave = false
                        saveSession()
                    }
                    if (!this.regions
                            .filter { it.isAttached }
                            .map { it.ID }
                            .contains(Session.get().region?.regionID ?: -1)
                    ) {
                        userRegionsLiveData.value =
                            ApiResponseState.ApiError(REGION_RESTRICTION_KAY, "")
                        clearSavedRegion()
                    }

                }
            }
        )
    }

    private fun clearSavedRegion() = viewModelScope.launch {
        userPreferencesRepository.saveRegion(null)
    }

    private fun saveSession() = viewModelScope.launch {
        userPreferencesRepository.saveUserSession(Session.get().getUserInfo())
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
                shouldSave = true
                getRegionForUser()
            }
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(userID: String): AddUserViewModel
    }
}
