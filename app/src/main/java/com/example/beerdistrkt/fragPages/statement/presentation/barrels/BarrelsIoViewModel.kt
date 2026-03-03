package com.example.beerdistrkt.fragPages.statement.presentation.barrels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.login.domain.model.Permission
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BARREL_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BOTTLE_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.MONEY
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatementItem
import com.example.beerdistrkt.fragPages.statement.domain.model.FStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.fragPages.statement.domain.usecase.DeleteRecordUseCase
import com.example.beerdistrkt.fragPages.statement.domain.usecase.GetBarrelStatementUseCase
import com.example.beerdistrkt.fragPages.statement.domain.usecase.GetFinanceStatementUseCase
import com.example.beerdistrkt.fragPages.statement.presentation.adapter.FStatementActionListener
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.adapter.BarrelStatementActionListener
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.mapper.BarrelUiMapper
import com.example.beerdistrkt.fragPages.statement.presentation.dialog.StatementOption
import com.example.beerdistrkt.fragPages.statement.presentation.mapper.FinanceStatementUiMapper
import com.example.beerdistrkt.fragPages.statement.presentation.model.BarrelStatementUiModel
import com.example.beerdistrkt.fragPages.statement.presentation.model.FStatementUiItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.SaleItemUiModel
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import com.example.beerdistrkt.utils.MITANA
import com.example.beerdistrkt.utils.MITANA_BOTTLE
import com.example.beerdistrkt.utils.M_OUT
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = BarrelsIoViewModel.Factory::class)
class BarrelsIoViewModel @AssistedInject constructor(
    private val getBarrelStatementUseCase: GetBarrelStatementUseCase,
    private val barrelUiMapper: BarrelUiMapper,
    private val deleteRecordUseCase: DeleteRecordUseCase,
    @Assisted val clientID: Int,
) : BaseViewModel(), BarrelStatementActionListener {

    private val _statementLiveData =
        MutableLiveData<ResultState<List<BarrelStatementUiModel>>>()
    val statementLiveData: LiveData<ResultState<List<BarrelStatementUiModel>>>
        get() = _statementLiveData

    private val _eventsFlow = MutableSharedFlow<UiEvent>()
    val eventsFlow: SharedFlow<UiEvent> = _eventsFlow.asSharedFlow()

    private val _apiState = MutableStateFlow<ResultState<Unit?>>(ResultState.Success(null))
    val apiState: StateFlow<ResultState<Unit?>> = _apiState.asStateFlow()

//    var isGroupedLiveData = MutableLiveData(true)

    private val statements = mutableListOf<BarrelStatementItem>()
    private val statementUiItems = mutableListOf<BarrelStatementUiModel>()

    private var totalCount = 1L
    private var oldestTime: String? = null
    private var firstOperationDate: String = String.empty()

    val isLastPage
        get() = firstOperationDate == oldestTime

    val isDataLoading
        get() = _statementLiveData.value is ResultState.Loading

    init {
        requestStatementList()
    }

    fun requestStatementList() {
        statements.clear()
        statementUiItems.clear()
        oldestTime = null
        loadMoreData()
    }

    fun loadMoreData() {
        println("kd_ ${statements.size} - $totalCount")
        if (statements.size < totalCount)
            getBarrelStatement()
    }

    private fun getBarrelStatement() {
        viewModelScope.launch {
            _statementLiveData.value = ResultState.Loading
            when (val result = getBarrelStatementUseCase(clientID, oldestTime)) {

                is ApiResponse.Error -> {
                    Log.d(TAG, "getBarrelStatement: ${result.message}")
                    _statementLiveData.value = ResultState.Error(result.statusCode)
                }

                is ApiResponse.Success -> {
                    println("KD_")
                    println(result.data)
                    totalCount = result.data.totalCount
                    oldestTime = result.data.statements.lastOrNull()?.dateStr
                    firstOperationDate = result.data.firstOperationDate
                    statements.addAll(result.data.statements)
                    statementUiItems.addAll(
                        result.data.statements.map(barrelUiMapper::map)
                    )
                    _statementLiveData.value = statementUiItems.toList().asSuccessState()
                }
            }
        }
    }

    fun deleteRecord(tableAndRecordId: Pair<StatementRecordType, Long>) {
        viewModelScope.launch {
            _apiState.emit(ResultState.Loading)
            val result = deleteRecordUseCase(
                recordID = tableAndRecordId.second.toString(),
                table = tableAndRecordId.first.name
            )
            _apiState.emit(result)
            when (result) {
                is ResultState.Error -> _eventsFlow.emit(UiEvent.ShowError(result.message.orEmpty()))

                ResultState.Loading -> {}
                is ResultState.Success<*> -> {
                    if (tableAndRecordId.first == StatementRecordType.SALE_BEER) {
                        _eventsFlow.emit(UiEvent.ShowDeleteSucceedWithUpdateRequest)
                    } else {
                        _eventsFlow.emit(UiEvent.ShowDeleteSucceed)
                    }

                    requestStatementList()
                }
            }
        }

        /*sendRequest(
            ApeniApiService.getInstance().deleteRecord(
                DeleteRequest(
                    id.toString(),
                    table,
                    session.userID ?: return
                )
            ),
            success = {
                requestStatementList()
                needUpdateLiveData.value = pagePos.toString()
            }
        )*/
    }

    private fun findItem(item: Any): FStatement? = when (item) {
//        is FStatementUiItem.Money -> statements.firstOrNull {
//            it is FStatement.PayMoney && it.recordId == item.recordId
//        }
//
//        is SaleItemUiModel -> statements.firstOrNull {
//            it is FStatement.SaleGroup && it.saleItems.any { saleItem ->
//                saleItem.recordId == item.recordId
//            }
//        }
//
        else -> null
    }

    private fun canChangeData(itemDate: FStatement?): Boolean {
        if (itemDate == null) return false

        return session.hasPermission(Permission.EditOldSale) ||
                (session.hasPermission(Permission.EditSale) && itemDate.isSoldToday)
    }

//    override fun onPaymentOptionClick(item: FStatementUiItem.Money) = onOptionClick(item)
//
//    override fun onSaleOptionClick(item: SaleItemUiModel) = onOptionClick(item)

    private fun onOptionClick(item: Any) {
        modifyingObject = item
        viewModelScope.launch {
            if (canChangeData(findItem(item)))
                _eventsFlow.emit(UiEvent.OpenOptions)
            else
                _eventsFlow.emit(UiEvent.CantModify)
        }
    }

    private var modifyingObject: Any? = null

    fun onActionSelected(action: StatementOption) = viewModelScope.launch {
        when (val item = modifyingObject) {
            is FStatementUiItem.Money -> when (action) {
                StatementOption.HISTORY -> _eventsFlow.emit(
                    UiEvent.GoHistory(Pair(MONEY, item.recordId))
                )

                StatementOption.EDIT -> _eventsFlow.emit(UiEvent.GoEdit(Pair(M_OUT, item.recordId)))
                StatementOption.DELETE -> _eventsFlow.emit(
                    UiEvent.DeleteConfirmation(StatementRecordType.TAKE_MONEY to item.recordId)
                )
            }

            is SaleItemUiModel -> when (action) {
                StatementOption.HISTORY -> {
                    when (item.recordType) {
                        StatementRecordType.SALE_BEER -> BARREL_DELIVERY
                        StatementRecordType.SALE_BOTTLE -> BOTTLE_DELIVERY
                        else -> null
                    }?.let { subject ->
                        _eventsFlow.emit(UiEvent.GoHistory(Pair(subject, item.recordId)))
                    }
                }

                StatementOption.EDIT -> {
                    when (item.recordType) {
                        StatementRecordType.SALE_BEER -> MITANA
                        StatementRecordType.SALE_BOTTLE -> MITANA_BOTTLE
                        else -> null
                    }?.let { op ->
                        _eventsFlow.emit(UiEvent.GoEdit(Pair(op, item.recordId)))
                    }
                }

                StatementOption.DELETE -> _eventsFlow.emit(
                    UiEvent.DeleteConfirmation(item.recordType to item.recordId)
                )
            }

            else -> {}
        }
    }

    override fun onOptionsClick(ids: List<Long>) {
        println("kd_ $ids")
    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): BarrelsIoViewModel
    }

    sealed interface UiEvent {
        data object OpenOptions : UiEvent
        data object CantModify : UiEvent
        data class ShowError(val msg: String) : UiEvent
        data object ShowDeleteSucceed : UiEvent
        data object ShowDeleteSucceedWithUpdateRequest : UiEvent
        data class GoEdit(val typeAndId: Pair<String, Long>) : UiEvent
        data class GoHistory(val subjectAndId: Pair<String, Long>) : UiEvent
        data class DeleteConfirmation(val tableAndId: Pair<StatementRecordType, Long>) : UiEvent
    }

    companion object {
        private const val COMMENT_SEPARATOR = " | "
    }

}
