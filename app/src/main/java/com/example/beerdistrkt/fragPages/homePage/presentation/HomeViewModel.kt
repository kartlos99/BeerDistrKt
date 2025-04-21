package com.example.beerdistrkt.fragPages.homePage.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.RefreshCustomersUseCase
import com.example.beerdistrkt.fragPages.homePage.domain.model.AddCommentModel
import com.example.beerdistrkt.fragPages.homePage.domain.model.CommentModel
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import com.example.beerdistrkt.fragPages.user.domain.usecase.RefreshUsersUseCase
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.waitFor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val refreshCustomers: RefreshCustomersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    override var session: Session,
    private val apeniApi: ApeniApiService,
) : BaseViewModel() {

    val mainLoaderLiveData = MutableLiveData<Boolean?>(null)

    private var currentDate = Calendar.getInstance()

    private val _barrelsListLiveData = MutableLiveData<ApiResponseState<List<SimpleBeerRowModel>>>()
    val barrelsListLiveData: LiveData<ApiResponseState<List<SimpleBeerRowModel>>>
        get() = _barrelsListLiveData

    private val _commentsListLiveData = MutableLiveData<List<CommentModel>>()
    val commentsListLiveData: LiveData<List<CommentModel>>
        get() = _commentsListLiveData

    private val _addCommentLiveData = MutableLiveData<ApiResponseState<String>>()
    val addCommentLiveData: LiveData<ApiResponseState<String>>
        get() = _addCommentLiveData

    private val _bottomSheetStateFlow = MutableStateFlow(0)
    val bottomSheetStateFlow = _bottomSheetStateFlow.asStateFlow()

    private val _storeHouseDataFlow: MutableStateFlow<ResultState<StoreHouseResponse>?> =
        MutableStateFlow(null)
//    val storeHouseDataFlow: StateFlow<StoreHouseResponse?> = _storeHouseDataFlow.asStateFlow()

    init {
        Log.d("homeVM", "init")

        getBeerUseCase.beerAsFlow().combine(_storeHouseDataFlow.asStateFlow()) { beer, data ->
            when (data) {
                is ResultState.Error -> {}

                is ResultState.Success -> if (!beer.isNullOrEmpty()) {
                    _barrelsListLiveData.value =
                        ApiResponseState.Success(formAllBarrelsList(data.data, beer))
                }

                else -> _barrelsListLiveData.value = ApiResponseState.Loading(true)
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            val userInfo = userPreferencesRepository.readUserSession()
            session.restoreFromSavedInfo(userInfo)
            checkToken()
            getComments()
        }
    }

    fun updateBottomSheetState(state: Int) = viewModelScope.launch {
        _bottomSheetStateFlow.emit(state)
    }

    fun setRegion(selectedRegion: WorkRegion) {
        viewModelScope.launch {
            userPreferencesRepository.saveRegion(selectedRegion)
            refreshCustomers()
            refreshUsersUseCase()
        }
        SharedPreferenceDataSource.getInstance().clearVersions()
        getStoreBalance()
        getComments()
    }


    fun getStoreBalance() = viewModelScope.launch {
        _storeHouseDataFlow.value = ResultState.Loading
        if (session.region?.hasOwnStorage == true)
            getRegionBalance()
        else
            getGlobalBalance()
    }

    private fun getRegionBalance() {
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseBalance(
                dateFormatDash.format(currentDate.time), 0
            ),
            successWithData = {
                Log.d("store", it.empty.toString())
                viewModelScope.launch {
                    _storeHouseDataFlow.emit(ResultState.Success(it))
                }
            },
        )
    }

    private fun getGlobalBalance() {
        sendRequest(
            ApeniApiService.getInstance().getGlobalBalance(
                dateFormatDash.format(currentDate.time)
            ),
            successWithData = {
                val valueOfDiff = mutableMapOf<Int, Int>()
                it.forEach { globalModel ->
                    valueOfDiff[globalModel.id] = globalModel.getBalance()
                }
                300 waitFor {
                    _barrelsListLiveData.value = ApiResponseState.Success(
                        listOf(
                            SimpleBeerRowModel(
                                BARRELS_IN_BREWERY_TITLE,
                                valueOfDiff
                            )
                        )
                    )
                }
            },
        )
    }

    private fun formAllBarrelsList(
        data: StoreHouseResponse?,
        beer: List<Beer>?,
    ): List<SimpleBeerRowModel> {
        if (beer.isNullOrEmpty() || data == null) return emptyList()

        val result = mutableListOf<SimpleBeerRowModel>()
        val a = data.full.groupBy { it.beerID }
        a.values.forEach {
            val valueOfDiff = mutableMapOf<Int, Int>()
            it.forEach { fbm ->
                valueOfDiff[fbm.barrelID] = fbm.inputToStore - fbm.saleCount
            }
            val currBeer = beer.firstOrNull { beerModel ->
                beerModel.id == it[0].beerID
            }
            val title = currBeer?.name ?: "_"
            result.add(SimpleBeerRowModel(title, valueOfDiff))
        }
        val valueOfDiff = mutableMapOf<Int, Int>()
        data.empty?.forEach { ebm ->
            valueOfDiff[ebm.barrelID] = ebm.inputEmptyToStore - ebm.outputEmptyFromStoreCount
        }
        result.add(SimpleBeerRowModel(HomeFragment.emptyBarrelTitle, valueOfDiff))
        return result
    }

    fun getComments() {
        sendRequest(
            apeniApi.getComments(),
            successWithData = {
                _commentsListLiveData.value = it
            }
        )
    }

    fun addComment(comment: AddCommentModel) {
        _addCommentLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addComment(comment),
            success = {
                getComments()
                _addCommentLiveData.value = ApiResponseState.Success(comment.comment)
            },
            finally = {
                _addCommentLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun stopAddCommentObserving() {
        _addCommentLiveData.value = ApiResponseState.Sleep
    }

    companion object {
        const val TAG = "homeVM"
        private const val BARRELS_IN_BREWERY_TITLE = "კასრები:საწარმოში"
    }
}