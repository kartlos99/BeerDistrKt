package com.example.beerdistrkt.fragPages.expense.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.fragPages.expense.domain.usecase.PutExpenseUseCase
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUserUseCase
import com.example.beerdistrkt.mapToString
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.parseDouble
import com.example.beerdistrkt.utils.Session
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

@HiltViewModel(assistedFactory = AddEditExpenseViewModel.Factory::class)
class AddEditExpenseViewModel @AssistedInject constructor(
    @Assisted
    private val expense: Expense?,
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
    private val putExpenseUseCase: PutExpenseUseCase,
    private val getUserUseCase: GetUserUseCase,
    override var session: Session,
) : BaseViewModel() {

    private val _categoriesStateFlow = MutableStateFlow<List<ExpenseCategory>>(listOf())
    val categoriesStateFlow = _categoriesStateFlow.asStateFlow()

    private val _errorStateFlow = MutableStateFlow("")
    val errorStateFlow = _errorStateFlow.asStateFlow()

    private val _uiEventsFlow = MutableSharedFlow<AddExpenseUiEvent>()
    val uiEventsFlow = _uiEventsFlow.asSharedFlow()

    private val _expenseState = MutableStateFlow(expense)
    val expenseState = _expenseState.asStateFlow()

    private val _expenseAmountState = MutableStateFlow(expense?.amount.mapToString())
    val expenseAmountState = _expenseAmountState.asStateFlow()

    init {
        getCategories()
    }

    private fun getCategories() = viewModelScope.launch {
        getExpenseCategoriesUseCase().collectLatest { result ->
            when (result) {
                is ResultState.Error -> {
                    _errorStateFlow.emit(result.message.orEmpty())
                }

                is ResultState.Success -> {
                    _categoriesStateFlow.emit(result.data.filter {
                        it.status == EntityStatus.ACTIVE || it.id == expense?.category?.id
                    })
                }

                ResultState.Loading -> {}
            }
        }
    }

    fun onDoneClick(
        amountStr: String,
        comment: String,
        categoryID: Int
    ) = viewModelScope.launch {
        _errorStateFlow.value = String.empty()
        val currentUser: User = getUserUseCase(session.userID!!)
            ?: throw NoSuchElementException("can't find user!")

        when {
            comment.length < 3 -> _errorStateFlow.emit(ERROR_TEXT_NO_COMMENT)
            amountStr.parseDouble() <= .0 -> _errorStateFlow.emit(ERROR_TEXT_NO_AMOUNT)
            else -> _categoriesStateFlow.value.firstOrNull {
                it.id == categoryID
            }?.let { category ->
                val expense = Expense(
                    expense?.id,
                    currentUser,
                    amountStr.parseDouble(),
                    comment,
                    dateTimeFormat.format(Calendar.getInstance().time),
                    category
                )
                putExpense(expense)
            }
                ?: _errorStateFlow.emit(ERROR_TEXT_NO_CATEGORY)
        }
    }

    fun setComment(comment: String) = _expenseState.update {
        it?.copy(comment = comment)
    }

    fun setAmount(amountStr: String) {
        _expenseAmountState.value = amountStr
    }

    fun setCategory(categoryID: Int) = _categoriesStateFlow.value.firstOrNull {
        it.id == categoryID
    }?.let { category ->
        _expenseState.update {
            it?.copy(category = category)
        }
    }.also {
        _errorStateFlow.value = String.empty()
    }

    private suspend fun putExpense(expense: Expense) {
        when (val result = putExpenseUseCase.invoke(expense)) {
            is ApiResponse.Error -> {
                _errorStateFlow.value = "FAILED: ${result.message}"
            }

            is ApiResponse.Success -> {
                _errorStateFlow.value = OPERATION_IS_DONE
                delay(DELAY_FOR_NAVIGATION_BACK)
                _uiEventsFlow.emit(AddExpenseUiEvent.GoBack)
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            expense?.id?.let {
                deleteExpense(it)
            } ?: _errorStateFlow.emit(ERROR_MESSAGE_EXPENSE_NOT_SAVED)
        }
    }

    private fun deleteExpense(id: String) {

        sendRequest(
            apiRequest = ApeniApiService.getInstance().deleteRecord(
                DeleteRequest(
                    id,
                    EXPENSE_TABLE_NAME,
                    session.userID!!
                )
            ),
            success = {
                viewModelScope.launch {
                    _errorStateFlow.value = OPERATION_IS_DONE
                    delay(DELAY_FOR_NAVIGATION_BACK)
                    _uiEventsFlow.emit(AddExpenseUiEvent.GoBack)
                }
            },
            finally = {}
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(expense: Expense?): AddEditExpenseViewModel
    }

    companion object {
        const val ERROR_TEXT_NO_CATEGORY = "აირჩიეთ კატეგორია!"
        const val ERROR_TEXT_NO_AMOUNT = "ჩაწერეთ ხარჯის ოდენობა!"
        const val ERROR_TEXT_NO_COMMENT = "ჩაწერეთ კომენტარი, მინ 3 სიმბოლო!"
        const val DELAY_FOR_NAVIGATION_BACK = 1200L
        const val EXPENSE_TABLE_NAME = "xarjebi"
        const val ERROR_MESSAGE_EXPENSE_NOT_SAVED = "მონაცემები არაა ბაზაში შენახული!"
        const val OPERATION_IS_DONE = "ოპერაცია შესრულებულია!"
    }
}