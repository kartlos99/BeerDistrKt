package com.example.beerdistrkt.fragPages.user.presentation.modify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.fragPages.user.data.model.AddUserRequestModel
import com.example.beerdistrkt.fragPages.user.domain.UserValidationResult
import com.example.beerdistrkt.fragPages.user.domain.UserValidator
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetRegionsUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUserUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.PutUserUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.RefreshUsersUseCase
import com.example.beerdistrkt.fragPages.user.presentation.model.ModifyUserData
import com.example.beerdistrkt.fragPages.user.presentation.model.RegionChipItem
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddUserViewModel.Factory::class)
class AddUserViewModel @AssistedInject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getUserUseCase: GetUserUseCase,
    private val putUserUseCase: PutUserUseCase,
    private val getRegionsUseCase: GetRegionsUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    @Assisted private val userID: String,
) : BaseViewModel() {

    private var shouldSave: Boolean = false

    private val _apiState = MutableStateFlow<ResultState<Unit?>>(ResultState.Success(null))
    val apiState: StateFlow<ResultState<Unit?>> = _apiState.asStateFlow()

    private val _uiState: MutableStateFlow<ModifyUserData> = MutableStateFlow(ModifyUserData())
    val uiState: StateFlow<ModifyUserData> = _uiState.asStateFlow()

    val deleteUserLiveData = MutableLiveData<ApiResponseState<String>>()

    val userValidatorLiveData = MutableLiveData<UserValidationResult>()

    private val allRegions = mutableListOf<WorkRegion>()

    private val attachedRegionIds = mutableSetOf<Int>()

    init {
        viewModelScope.launch {
            allRegions.clear()
            allRegions.addAll(getRegionsUseCase())
            var user: User? = null
            if (userID.isNotEmpty()) {
                user = getUserUseCase(userID)
                attachedRegionIds.addAll(user?.regions?.map { it.id }.orEmpty())
            } else {
                attachedRegionIds.add(Session.get().region?.id ?: -1)
            }
            _uiState.emit(
                ModifyUserData(
                    user,
                    Session.get().hasPermission(Permission.ManageRegion),
                    generateRegionChipsData()
                )
            )
        }
    }

    private fun generateRegionChipsData(): List<RegionChipItem> {
        return allRegions.map { workRegion ->
            RegionChipItem(
                id = workRegion.id,
                name = workRegion.name,
                code = workRegion.code,
                isAttached = attachedRegionIds.contains(workRegion.id)
            )
        }
    }

    fun saveUserData() {
        val userData = _uiState.value
        if (userData.user == null) return

        val userValidatorResult = UserValidator(
            userData.user,
            userData.isChangingPassword,
            userData.password,
            userData.confirmPassword,
            attachedRegionIds,
        ).validate()

        val requestModel = AddUserRequestModel(
            userData.user,
            userData.password,
            userData.isChangingPassword,
            attachedRegionIds
        )

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

    private fun updateLocalSession() {
        Session.get().regions.clear()
        Session.get().regions.addAll(
            allRegions.filter { attachedRegionIds.contains(it.id) }
        )
        if (shouldSave) {
            shouldSave = false
            saveSession()
        }
        if (!attachedRegionIds.contains(Session.get().region?.id ?: -1)) {
//            userRegionsLiveData.value =
//                ApiResponseState.ApiError(REGION_RESTRICTION_KAY, "")
            clearSavedRegion()
        }

    }

    private fun clearSavedRegion() = viewModelScope.launch {
        userPreferencesRepository.saveRegion(null)
    }

    private fun saveSession() = viewModelScope.launch {
        userPreferencesRepository.saveUserSession(Session.get().getUserInfo())
    }

    fun getAllRegionNames(): Array<String> {
        return allRegions.map { it.name }.toTypedArray()
    }

    fun getSelectedRegions(): BooleanArray {
        return allRegions.map { attachedRegionIds.contains(it.id) }.toBooleanArray()
    }

    fun setNewRegions() {
        val request = UserAttachRegionsRequest(
            userID,
            attachedRegionIds,
        )
        sendRequest(
            ApeniApiService.getInstance().setRegions(request),
            successWithData = {
                shouldSave = true
                viewModelScope.launch {
                    refreshUsersUseCase()
                    _uiState.update {
                        it.copy(
                            user = getUserUseCase(userID),
                            regionChips = generateRegionChipsData()
                        )
                    }
                    if (Session.get().userID == userID) {
                        updateLocalSession()
                    }
                }
            }
        )
    }

    fun saveFormState(
        readUser: User,
        regionIds: MutableList<Int>,
        isChangingPassword: Boolean,
        password: String,
        confirmPassword: String,
    ) {
        if (Session.get().hasPermission(Permission.ManageRegion)) {
            attachedRegionIds.clear()
            attachedRegionIds.addAll(regionIds)
        }
        _uiState.update { userData ->
            userData.copy(
                user = readUser.copy(
                    regions = allRegions.filter { region -> attachedRegionIds.contains(region.id) }
                ),
                regionChips = generateRegionChipsData(),
                isChangingPassword = isChangingPassword,
                password = password,
                confirmPassword = confirmPassword,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(userID: String): AddUserViewModel
    }

    companion object {
        const val REGION_RESTRICTION_KAY: Int = 234
    }
}
