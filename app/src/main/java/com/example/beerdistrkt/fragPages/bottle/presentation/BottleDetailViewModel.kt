package com.example.beerdistrkt.fragPages.bottle.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.domain.model.BottleStatus
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.DeleteBottleUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottleUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.PutBottleUseCase
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

    private val _currentBottleStateFlow = MutableStateFlow(Bottle.newInstance())
    val currentBottleStateFlow: StateFlow<Bottle> = _currentBottleStateFlow.asStateFlow()

    private val _apiStateFlow: MutableStateFlow<ResultState<List<Bottle>>> =
        MutableStateFlow(emptyList<Bottle>().asSuccessState())
    val apiStateFlow: StateFlow<ResultState<List<Bottle>>> = _apiStateFlow.asStateFlow()


    private var beerList: List<Beer> = listOf()

    init {
        viewModelScope.launch {
            beerList = getBeerUseCase()
            _currentBottleStateFlow.emit(
                getBottleUseCase(bottleID) ?: Bottle.newInstance()
            )
        }
    }

    private fun throwEvent(@StringRes msgID: Int) {
        viewModelScope.launch {
            eventsFlow.emit(Event.IncorrectDataEntered(msgID))
        }
    }

    private fun isPriceValid(price: String): Double? {
        return try {
            val double = price.toDouble()
            if (double > 0)
                double
            else {
                throwEvent(R.string.incorrect_price)
                null
            }
        } catch (e: Exception) {
            throwEvent(R.string.incorrect_price)
            null
        }
    }

    private fun isBeerValid(beerName: String): Int? {
        val ss = beerList.firstOrNull {
            it.name == beerName
        }
        return if (ss == null) {
            throwEvent(R.string.incorrect_beer)
            null
        } else {
            ss.id
        }
    }

    private fun isVolumeValid(volume: String): Double? {
        return try {
            volume.toDouble()
        } catch (e: Exception) {
            throwEvent(R.string.incorrect_volume)
            null
        }
    }

    private fun isNameValid(name: String): String? {
        return if (name.trim().length > 3) {
            name.trim()
        } else {
            throwEvent(R.string.incorrect_bottle_name)
            null
        }
    }


    fun getBeerNames(): List<String> {
        return beerList
            .filter { it.isActive }
            .map { it.name }
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
        saveBottle(_currentBottleStateFlow.value)
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
        val volume = try {
            volumeStr.toDouble()
        } catch (e: NumberFormatException) {
            .0
        }
        _currentBottleStateFlow.update {
            it.copy(
                volume = volume,
                actualVolume = volume,
            )
        }
    }

    fun setBeer(beerIndexInAdapter: Int) {
        val beer = try {
            beerList.filter { it.isActive }[beerIndexInAdapter]
        } catch (e: Exception) {
            Beer.newInstance()
        }
        _currentBottleStateFlow.update {
            it.copy(beer = beer)
        }
    }

    fun setPrice(priceStr: String) {
        val price = try {
            priceStr.toDouble()
        } catch (e: NumberFormatException) {
            .0
        }
        _currentBottleStateFlow.update {
            it.copy(price = price)
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