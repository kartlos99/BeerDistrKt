package com.example.beerdistrkt.fragPages.bottle.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.domain.model.ValidationResult
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.domain.model.BottleStatus
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.DeleteBottleUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottleUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.PutBottleUseCase
import com.example.beerdistrkt.fragPages.bottle.presentation.model.BottleUiModel
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = BottleDetailViewModel.Factory::class)
class BottleDetailViewModel @AssistedInject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottleUseCase: GetBottleUseCase,
    private val putBottleUseCase: PutBottleUseCase,
    private val deleteBottleUseCase: DeleteBottleUseCase,
    @Assisted private val bottleID: Int
) : BaseViewModel() {

    val eventsFlow = MutableSharedFlow<Event>()

    private val _currentBottleStateFlow = MutableStateFlow(BottleUiModel())
    val currentBottleStateFlow: StateFlow<BottleUiModel> = _currentBottleStateFlow.asStateFlow()

    private val _apiStateFlow: MutableStateFlow<ResultState<List<Bottle>>> =
        MutableStateFlow(emptyList<Bottle>().asSuccessState())
    val apiStateFlow: StateFlow<ResultState<List<Bottle>>> = _apiStateFlow.asStateFlow()

    private val _bottleStatusesFlow = MutableStateFlow(listOf<Int>())
    val bottleStatusesFlow = _bottleStatusesFlow.asStateFlow()

    private val _availableBeersFlow = MutableStateFlow(listOf<String>())
    val availableBeersFlow = _availableBeersFlow.asStateFlow()

    private var beerList: List<Beer> = listOf()

    init {
        viewModelScope.launch {
            beerList = getBeerUseCase()
            _bottleStatusesFlow.emit(
                BottleStatus.entries
                    .filter { it == BottleStatus.ACTIVE || it == BottleStatus.INACTIVE }
                    .map { it.displayName }
            )
            _currentBottleStateFlow.emit(
                getBottleUseCase(bottleID)?.let {
                    mapToUiModel(it)
                } ?: BottleUiModel()
            )
            _availableBeersFlow.emit(
                getAvailableBeers().map { it.name }
            )
        }
    }

    private fun mapToUiModel(bottle: Bottle): BottleUiModel = BottleUiModel(
        id = bottle.id,
        name = bottle.name,
        volume = bottle.volume.toString(),
        beer = bottle.beer,
        price = bottle.price.toString(),
        status = bottle.status,
        sortValue = bottle.sortValue,
        imageFileName = bottle.imageFileName
    )

    private fun throwEvent(@StringRes msgID: Int) {
        viewModelScope.launch {
            eventsFlow.emit(Event.IncorrectDataEntered(msgID))
        }
    }

    private fun getAvailableBeers(): List<Beer> {
        return beerList
            .filter { it.isActive || it.id == _currentBottleStateFlow.value.beer?.id }
    }

    fun deleteBottle() {
        if (bottleID > 0)
            deleteBottle(bottleID)
        else
            throwEvent(R.string.error_cant_delete_unsaved_data)
    }

    private fun deleteBottle(bottleID: Int) {
        viewModelScope.launch {
            _apiStateFlow.emit(ResultState.Loading)
            _apiStateFlow.emit(deleteBottleUseCase(bottleID).toResultState())
        }
    }

    fun onSaveClicked() {
        val validator = BottleValidator()
        when (val result = validator.getBottleStatus(_currentBottleStateFlow.value)) {
            is ValidationResult.IsValid -> saveBottle(result.item)
            is ValidationResult.NotValid -> throwEvent(result.message)
        }
    }

    private fun saveBottle(bottle: Bottle) {
        viewModelScope.launch {
            _apiStateFlow.emit(ResultState.Loading)
            _apiStateFlow.emit(putBottleUseCase(bottle).toResultState())
        }
    }

    fun setName(name: String) {
        _currentBottleStateFlow.update {
            it.copy(name = name)
        }
    }

    fun setVolume(volumeStr: String) {
        _currentBottleStateFlow.update {
            it.copy(volume = volumeStr)
        }
    }

    fun setBeer(beerIndexInAdapter: Int) {
        val beer = try {
            getAvailableBeers()[beerIndexInAdapter]
        } catch (e: Exception) {
            Beer.newInstance()
        }
        _currentBottleStateFlow.update {
            it.copy(beer = beer)
        }
    }

    fun setPrice(priceStr: String) {
        _currentBottleStateFlow.update {
            it.copy(price = priceStr)
        }
    }

    fun setStatus(status: BottleStatus) {
        _currentBottleStateFlow.update {
            it.copy(status = status)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted bottleID: Int
        ): BottleDetailViewModel
    }
}