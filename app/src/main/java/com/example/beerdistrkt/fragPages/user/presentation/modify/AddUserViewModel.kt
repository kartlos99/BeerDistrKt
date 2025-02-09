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
import com.example.beerdistrkt.fragPages.user.domain.usecase.DeleteUserUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetRegionsUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUserUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.PutUserUseCase
import com.example.beerdistrkt.fragPages.user.presentation.model.ModifyUserData
import com.example.beerdistrkt.fragPages.user.presentation.model.RegionChipItem
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.toResultState
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
    private val deleteUserUseCase: DeleteUserUseCase,
    override var session: Session,
    @Assisted private val userID: String,
) : BaseViewModel() {

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
                attachedRegionIds.add(session.region?.id ?: -1)
            }
            _uiState.emit(
                ModifyUserData(
                    user,
                    session.hasPermission(Permission.ManageRegion),
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
            when (val result = putUserUseCase(model)) {
                is ApiResponse.Error -> {
                    _apiState.emit(result.toResultState())
                }

                is ApiResponse.Success -> {
                    _apiState.emit(result.toResultState().transform { })
                    if (session.userID == userID) {
                        updateLocalSession()
                    }
                }
            }
        }
    }

    fun deleteUser() {
        if (userID.isNotEmpty())
            viewModelScope.launch {
                _apiState.emit(ResultState.Loading)
                _apiState.emit(deleteUserUseCase(userID))
            }
    }

    private fun updateLocalSession() {
        session.regions.clear()
        session.regions.addAll(
            allRegions.filter { attachedRegionIds.contains(it.id) }
        )
        saveSession()
        if (!attachedRegionIds.contains(session.region?.id ?: -1)) {
//            userRegionsLiveData.value =
//                ApiResponseState.ApiError(REGION_RESTRICTION_KAY, "")
            clearSavedRegion()
            forceLogout()
        }
    }

    private fun clearSavedRegion() = viewModelScope.launch {
        userPreferencesRepository.saveRegion(null)
    }

    private fun saveSession() = viewModelScope.launch {
        userPreferencesRepository.saveUserSession(session.getUserInfo())
    }

    fun saveFormState(
        readUser: User,
        regionIds: MutableList<Int>,
        isChangingPassword: Boolean,
        password: String,
        confirmPassword: String,
    ) {
        if (session.hasPermission(Permission.ManageRegion)) {
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
}
