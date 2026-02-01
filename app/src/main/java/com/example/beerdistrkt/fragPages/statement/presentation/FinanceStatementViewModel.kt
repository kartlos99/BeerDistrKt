package com.example.beerdistrkt.fragPages.statement.presentation

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
import com.example.beerdistrkt.fragPages.statement.domain.model.FStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.fragPages.statement.domain.usecase.GetFinanceStatementUseCase
import com.example.beerdistrkt.fragPages.statement.presentation.adapter.FStatementActionListener
import com.example.beerdistrkt.fragPages.statement.presentation.dialog.StatementOption
import com.example.beerdistrkt.fragPages.statement.presentation.mapper.FinanceStatementUiMapper
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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = FinanceStatementViewModel.Factory::class)
class FinanceStatementViewModel @AssistedInject constructor(
    private val getFinanceStatementUseCase: GetFinanceStatementUseCase,
    private val financeStatementUiMapper: FinanceStatementUiMapper,
    @Assisted val clientID: Int,
) : BaseViewModel(), FStatementActionListener {

    private val _statementLiveData =
        MutableLiveData<ResultState<List<FStatementUiItem>>>()
    val statementLiveData: LiveData<ResultState<List<FStatementUiItem>>>
        get() = _statementLiveData

    private val _eventsFlow = MutableSharedFlow<UiEvent>()
    val eventsFlow: SharedFlow<UiEvent> = _eventsFlow.asSharedFlow()

    var isGroupedLiveData = MutableLiveData(true)

    private val statement = mutableListOf<FStatement>()
    private val statementUiItems = mutableListOf<FStatementUiItem>()

    val needUpdateLiveData = MutableLiveData<String?>(null)
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
        statement.clear()
        loadMoreData()
    }

    fun loadMoreData() {
        if (statement.size < totalCount)
            getFinanceStatement()
    }

    private fun getFinanceStatement() {
        viewModelScope.launch {
            _statementLiveData.value = ResultState.Loading
            when (val result = getFinanceStatementUseCase(clientID, oldestTime)) {

                is ApiResponse.Error -> {
                    Log.d(TAG, "getFinanceStatement: ${result.message}")
                    _statementLiveData.value = ResultState.Error(result.statusCode)
                }

                is ApiResponse.Success -> {
                    totalCount = result.data.totalCount
                    oldestTime = result.data.statements.lastOrNull()?.dateStr
                    firstOperationDate = result.data.firstOperationDate
                    statement.addAll(result.data.statements)
                    statementUiItems.addAll(
                        result.data.statements.map(financeStatementUiMapper::map)
                    )
                    _statementLiveData.value = statementUiItems.toList().asSuccessState()
                }
            }
        }
    }

    fun deleteRecord(recType: StatementRecordType, recordId: Long) {
        return
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
        is FStatementUiItem.Money -> statement.firstOrNull {
            it is FStatement.PayMoney && it.recordId == item.recordId
        }

        is SaleItemUiModel -> statement.firstOrNull {
            it is FStatement.SaleGroup && it.saleItems.any { saleItem ->
                saleItem.recordId == item.recordId
            }
        }

        else -> null
    }

    private fun canChangeData(itemDate: FStatement?): Boolean {
        if (itemDate == null) return false

        return session.hasPermission(Permission.EditOldSale) ||
                (session.hasPermission(Permission.EditSale) && itemDate.isSoldToday)
    }

    override fun onPaymentOptionClick(item: FStatementUiItem.Money) = onOptionClick(item)

    override fun onSaleOptionClick(item: SaleItemUiModel) = onOptionClick(item)

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
                StatementOption.DELETE -> _eventsFlow.emit(UiEvent.DeleteConfirmation)
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

                StatementOption.DELETE -> _eventsFlow.emit(UiEvent.DeleteConfirmation)
            }

            else -> {}
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): FinanceStatementViewModel
    }

    sealed interface UiEvent {
        data object OpenOptions : UiEvent
        data object CantModify : UiEvent
        data class GoEdit(val typeAndId: Pair<String, Long>) : UiEvent
        data class GoHistory(val subjectAndId: Pair<String, Long>) : UiEvent
        data object DeleteConfirmation : UiEvent
    }

    companion object {
        private const val COMMENT_SEPARATOR = " | "
    }
}
