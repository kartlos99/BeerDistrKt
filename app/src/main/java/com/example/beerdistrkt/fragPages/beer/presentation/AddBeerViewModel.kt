package com.example.beerdistrkt.fragPages.beer.presentation

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.beer.domain.usecase.PutBeerUseCase
import com.example.beerdistrkt.fragPages.beer.domain.usecase.UpdateBeerPositionUseCase
import com.example.beerdistrkt.mapToString
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import com.example.beerdistrkt.parseDouble
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBeerViewModel @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val updateBeerPositionUseCase: UpdateBeerPositionUseCase,
    private val putBeerUseCase: PutBeerUseCase,
) : BaseViewModel() {

    val beerList = ObjectCache.getInstance()
        .getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID)
        ?.filter { it.isActive }
        ?: mutableListOf()

    private val _beersFlow: MutableStateFlow<ResultState<List<Beer>>> = MutableStateFlow(ResultState.Loading)
    val beersFlow: StateFlow<ResultState<List<Beer>>> = _beersFlow.asStateFlow()

    private val _currentBeerStateFlow: MutableStateFlow<Beer?> = MutableStateFlow(null)
    val currentBeerStateFlow: StateFlow<Beer?> = _currentBeerStateFlow.asStateFlow()

    private val _addBeerLiveData = MutableLiveData<ApiResponseState<String>>()
    val addBeerLiveData: LiveData<ApiResponseState<String>>
        get() = _addBeerLiveData

    private val _deleteBeerLiveData = MutableLiveData<ApiResponseState<String>>()
    val deleteBeerLiveData: LiveData<ApiResponseState<String>>
        get() = _deleteBeerLiveData

    private val _priceState = MutableStateFlow(String.empty())
    val priceState = _priceState.asStateFlow()

    init {
        initBeers()
    }

    private fun initBeers() = viewModelScope.launch {
        _beersFlow.emit(
            getBeerUseCase.invoke()
                .filter { it.isActive }
                .sortedBy { it.sortValue }
                .asSuccessState()
        )
    }

    fun initNewBeer() {
        viewModelScope.launch {
            _currentBeerStateFlow.emit(
                Beer(
                    name = String.empty(),
                    sortValue = .0
                )
            )
            _priceState.emit(String.empty())
        }
    }

    fun saveChanges() {
        _currentBeerStateFlow.value?.let { beer ->
            viewModelScope.launch {
                _beersFlow.emit(ResultState.Loading)
                val result = putBeerUseCase(
                    beer.copy(
                        name = beer.name.trim(),
                        price = _priceState.value.parseDouble()
                    )
                )
                when (result) {
                    is ApiResponse.Error -> {
                        _beersFlow.emit(result.toResultState())
                    }

                    is ApiResponse.Success -> {
                        _beersFlow.emit(
                            result.data
                                .filter { it.isActive }
                                .sortedBy { it.sortValue }
                                .asSuccessState()
                        )
                        _currentBeerStateFlow.emit(null)
                        _priceState.update { String.empty() }
                    }
                }
            }
        }
    }

/*
    fun sendDataToDB(beer: BeerModelBase) {

        _addBeerLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addBeer(beer),
            successWithData = {
                _addBeerLiveData.value = ApiResponseState.Success(it)
            },
            responseFailure = { code, error ->
                _addBeerLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                _addBeerLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }
*/

    fun removeBeer(beerId: Int) {

        _deleteBeerLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().deleteBeer(DeleteBeerModel(beerId)),
            successWithData = {
                _deleteBeerLiveData.value = ApiResponseState.Success(it)
            },
            responseFailure = { code, error ->
                _deleteBeerLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                _deleteBeerLiveData.value = ApiResponseState.Loading(false)
            }
        )

    }

    fun onItemMove(startPosition: Int, endPosition: Int) = viewModelScope.launch {

        val beerList = getBeerUseCase()
            .filter { it.isActive }
            .sortedBy { it.sortValue }

        val newSortValue = when {
            endPosition == 0 -> (beerList.firstOrNull()?.sortValue ?: .0) - 1
            endPosition == beerList.lastIndex -> (beerList.lastOrNull()?.sortValue ?: .0) + 1
            startPosition < endPosition -> {
                (beerList[endPosition].sortValue + beerList[endPosition + 1].sortValue) / 2
            }

            startPosition > endPosition -> {
                (beerList[endPosition].sortValue + beerList[endPosition - 1].sortValue) / 2
            }

            else -> .0
        }
        updateBeerPositionUseCase.invoke(
            beerList[startPosition].id,
            newSortValue
        )
    }

    fun clearBeerData() {
        viewModelScope.launch {
            _currentBeerStateFlow.emit(null)
            _priceState.emit(String.empty())
        }
    }

    fun editBeer(beer: Beer) {
        viewModelScope.launch {
            _currentBeerStateFlow.emit(beer)
            _priceState.emit(beer.price.mapToString())
        }
    }

    fun setBeerColor(color: Int) {
        _currentBeerStateFlow.update {
            it?.copy(displayColor = color)
        }
    }

    fun setBeerName(name: String) {
        _currentBeerStateFlow.update {
            it?.copy(name = name)
        }
    }

    fun setBeerPrice(price: String) {
        _priceState.update {
            price
        }
    }

    val currentBeerColor: Int
        get() = _currentBeerStateFlow.value?.displayColor ?: Color.rgb(128, 128, 128)

}
