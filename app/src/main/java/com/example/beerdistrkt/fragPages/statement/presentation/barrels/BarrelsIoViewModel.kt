package com.example.beerdistrkt.fragPages.statement.presentation.barrels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.login.domain.model.Permission
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelIo
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatementItem
import com.example.beerdistrkt.fragPages.statement.domain.usecase.DeleteRecordUseCase
import com.example.beerdistrkt.fragPages.statement.domain.usecase.GetBarrelStatementUseCase
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.adapter.BarrelStatementActionListener
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.mapper.BarrelUiMapper
import com.example.beerdistrkt.fragPages.statement.presentation.dialog.StatementOption
import com.example.beerdistrkt.fragPages.statement.presentation.model.BarrelStatementUiModel
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import com.example.beerdistrkt.utils.K_OUT
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

    private val statements = mutableListOf<BarrelStatementItem>()
    private val statementUiItems = mutableListOf<BarrelStatementUiModel>()

    private var totalCount = 1L
    private var oldestTime: String? = null
    private var firstOperationDate: String = String.empty()

    private var modifyingBarrelIo: BarrelIo? = null

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
        if (statements.size < totalCount)
            getBarrelStatement()
    }

    private fun getBarrelStatement() {
        viewModelScope.launch {
            _statementLiveData.value = ResultState.Loading
            when (val result = getBarrelStatementUseCase(clientID, oldestTime)) {

                is ApiResponse.Error -> {
                    _statementLiveData.value = ResultState.Error(result.statusCode)
                }

                is ApiResponse.Success -> {
                    totalCount = result.data.totalCount
                    oldestTime = result.data.statements.lastOrNull()?.dateStr
                    firstOperationDate = result.data.firstOperationDate
                    statements.addAll(result.data.statements)

                    val nextPart = result.data.statements.mapIndexed { index, barrelStatementItem ->
                        barrelUiMapper.map(barrelStatementItem).apply {
                            isExpanded = statementUiItems.isEmpty() && index == 0
                        }
                    }
                    statementUiItems.addAll(nextPart)
                    _statementLiveData.value = statementUiItems.toList().asSuccessState()
                }
            }
        }
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            _apiState.emit(ResultState.Loading)
            val result = deleteRecordUseCase(
                recordID = recordId.toString(),
                table = K_OUT,
            )
            _apiState.emit(result)
            when (result) {
                is ResultState.Error -> _eventsFlow.emit(UiEvent.ShowError(result.message.orEmpty()))

                ResultState.Loading -> {}
                is ResultState.Success<*> -> {
                    _eventsFlow.emit(UiEvent.ShowDeleteSucceed)

                    requestStatementList()
                }
            }
        }
    }

    private fun canChangeData(itemDate: BarrelStatementItem): Boolean {

        return session.hasPermission(Permission.EditOldSale) ||
                (session.hasPermission(Permission.EditSale) && itemDate.isRegisteredToday)
    }

    fun onActionSelected(action: StatementOption) = viewModelScope.launch {
        val recordId = modifyingBarrelIo?.recId ?: return@launch
        when (action) {
            StatementOption.HISTORY -> {}
            StatementOption.EDIT -> _eventsFlow.emit(UiEvent.GoEdit(Pair(K_OUT, recordId)))
            StatementOption.DELETE -> _eventsFlow.emit(UiEvent.DeleteConfirmation(recordId))
        }
    }

    fun onModifyingOutputSelected(ioItem: BarrelIo?) {
        viewModelScope.launch {
            modifyingBarrelIo = ioItem
            if (ioItem != null)
                _eventsFlow.emit(UiEvent.OpenOptions)
        }
    }

    override fun onOptionsClick(ids: List<Long>) {
        viewModelScope.launch {
            statements.firstOrNull { statement ->
                statement.ioItems.map { it.recId } == ids
            }?.let { statement ->
                if (canChangeData(statement)) {
                    val outputs = statement.ioItems.filter { it.countOut > 0 }
                    when (outputs.size) {
                        0 -> _eventsFlow.emit(UiEvent.CantModifyInputs)
                        1 -> {
                            modifyingBarrelIo = outputs.first()
                            _eventsFlow.emit(UiEvent.OpenOptions)
                        }

                        else -> {
                            _eventsFlow.emit(UiEvent.SelectModifyingOutput(outputs))
                        }
                    }
                } else
                    _eventsFlow.emit(UiEvent.CantModify)
            }
        }

    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): BarrelsIoViewModel
    }

    sealed interface UiEvent {
        data object OpenOptions : UiEvent
        data object CantModify : UiEvent
        data object CantModifyInputs : UiEvent
        data class SelectModifyingOutput(val items: List<BarrelIo>) : UiEvent
        data class ShowError(val msg: String) : UiEvent
        data object ShowDeleteSucceed : UiEvent
        data class GoEdit(val typeAndId: Pair<String, Long>) : UiEvent

        /* on this page we can delete only one statement, BarrelOutput => 'kout' */
        data class DeleteConfirmation(val recordId: Long) : UiEvent
    }

}
