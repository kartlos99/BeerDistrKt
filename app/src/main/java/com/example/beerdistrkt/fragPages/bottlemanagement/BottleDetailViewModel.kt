package com.example.beerdistrkt.fragPages.bottlemanagement

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.BottleStatus
import com.example.beerdistrkt.models.bottle.DefaultBottleDtoMapper
import com.example.beerdistrkt.models.bottle.dto.BaseBottleModelDto
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BottleDetailViewModel(
    private val bottleID: Int
) : BaseViewModel() {

    val eventsFlow = MutableSharedFlow<Event>()
    val stateFlow = MutableStateFlow<Event>(Event.ShowLoading(false))

    private val bottleList = ObjectCache.getInstance()
        .getList(BaseBottleModel::class, ObjectCache.BOTTLE_LIST_ID) ?: mutableListOf()
    private val beerList = ObjectCache.getInstance()
        .getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID) ?: mutableListOf()

    init {
        if (bottleID > 0) {
            bottleList.firstOrNull {
                it.id == bottleID
            }?.let {
                viewModelScope.launch {
                    stateFlow.emit(Event.EditBottle(it))
                }
            }
        }
    }

    fun readForm(
        name: String,
        volume: String,
        beerName: String,
        price: String,
        bottleStatus: BottleStatus?
    ) {
        if (bottleStatus == null) {
            throwEvent(R.string.incorrect_status)
            return
        }
        saveBottle(
            BaseBottleModelDto(
                bottleID,
                isNameValid(name) ?: return,
                isVolumeValid(volume) ?: return,
                isBeerValid(beerName) ?: return,
                isPriceValid(price) ?: return,
                bottleStatus
            )
        )
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
            it.dasaxeleba == beerName
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

    private fun saveBottle(bottleModel: BaseBottleModelDto) {
        viewModelScope.launch {
            eventsFlow.emit(Event.ShowLoading(true))
        }
        sendRequest(
            ApeniApiService.getInstance().saveBottle(bottleModel),
            successWithData = {
                val bottleMapper = DefaultBottleDtoMapper(beerList)
                val bottles = it.map { dto ->
                    bottleMapper.map(dto)
                }
                ObjectCache.getInstance()
                    .putList(BaseBottleModel::class, ObjectCache.BOTTLE_LIST_ID, bottles)
                viewModelScope.launch {
                    eventsFlow.emit(Event.DataSaved)
                }
            },
            responseFailure = { code: Int, error: String ->
                viewModelScope.launch {
                    eventsFlow.emit(Event.Error(code, error))
                }
            },
            finally = {
                viewModelScope.launch {
                    eventsFlow.emit(Event.ShowLoading(false))
                }
            }
        )
    }

    fun getBeerNames(): List<String> {
        return beerList
            .filter { it.isActive }
            .map { it.dasaxeleba ?: "-" }
    }
}