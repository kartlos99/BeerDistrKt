package com.example.beerdistrkt.fragPages.statement.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.PaginatedScrollListener
import com.example.beerdistrkt.databinding.StatementSubPageFragmentBinding
import com.example.beerdistrkt.fragPages.login.domain.model.Permission
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BARREL_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BOTTLE_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.MONEY
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.fragPages.statement.model.CtxMenuItem
import com.example.beerdistrkt.fragPages.statement.presentation.adapter.FStatementAdapter
import com.example.beerdistrkt.fragPages.statement.presentation.model.FinanceStatementUiModel
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.orZero
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.OBJ_ID
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FinanceStatementFragment : BaseFragment<FinanceStatementViewModel>() {

    private val binding by viewBinding(StatementSubPageFragmentBinding::bind)

    private var pagePos: Int = 0

    var action: ((operation: String, recordID: Long) -> Unit)? = null
    var updateAnotherPage: (() -> Unit)? = null
    var onShowHistory: ((recordID: Int, historyOf: String) -> Unit)? = null
    private lateinit var fAdapter: FStatementAdapter

    private val clientID by lazy {
        arguments?.getInt(OBJ_ID).orZero()
    }

    override val viewModel by paramViewModels<FinanceStatementViewModel, FinanceStatementViewModel.Factory> { factory ->
        factory.create(clientID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.statement_sub_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRecycler()
        observeData()
    }

    private fun initView() = with(binding) {
        amoColumnTitle1.text = getString(R.string.text_tarigi)
        amoColumnTitle2.text = getString(R.string.price)
        amoColumnTitle3.text = getString(R.string.pay)
        amoColumnTitle4.text = getString(R.string.davalianeba)
    }

    private fun initRecycler() = with(binding.statementSubPageRc) {
        val linearLayoutManager = LinearLayoutManager(context)
        fAdapter = FStatementAdapter(
            viewModel.session.hasPermission(Permission.EditOldSale),
            viewModel.session.hasPermission(Permission.EditSale),
        ) {
            return@FStatementAdapter viewModel.isGroupedLiveData.value ?: true
        }
        layoutManager = linearLayoutManager
        this.adapter = fAdapter
        addOnScrollListener(PaginatedListener(linearLayoutManager))
    }

    private fun observeData() {
        viewModel.isGroupedLiveData.observe(viewLifecycleOwner) { grouped: Boolean ->
//            statementListAdapter.isGrouped = grouped
        }
        viewModel.statementLiveData.observe(viewLifecycleOwner) { result ->
            binding.statementProgressBar.isVisible = result is ResultState.Loading
            result.onSuccess {
                fAdapter.submitList(it)
            }
        }
        viewModel.needUpdateLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                updateAnotherPage?.invoke()
                viewModel.needUpdateLiveData.value = null
            }
        }
    }

    fun changeStatementAppearance(grouped: Boolean) {
        viewModel.changeDataStructure(grouped)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            CtxMenuItem.Edit.itemID -> if (pagePos == 0) {
                val statementItem = fAdapter.getClickedItem(item.groupId)
                action?.invoke(statementItem.recordType.name, statementItem.recordId.orZero())
                true
            } else false

            CtxMenuItem.EditBarrel.itemID -> if (pagePos == 1) {
                val statementItem = fAdapter.getClickedItem(item.groupId)
                action?.invoke(statementItem.recordType.name, statementItem.recordId.orZero())
                true
            } else false

            CtxMenuItem.Delete.itemID -> if (pagePos == 0) {
                confirmDeleteStatement(fAdapter.getClickedItem(item.groupId))
                true
            } else false

            CtxMenuItem.DeleteBarrel.itemID -> if (pagePos == 1) {
                confirmDeleteStatement(fAdapter.getClickedItem(item.groupId))
                true
            } else false

            CtxMenuItem.History.itemID -> {
                val statementItem = fAdapter.getClickedItem(item.groupId)
                if (statementItem.pay != .0)
                    onShowHistory?.invoke(statementItem.recordId.orZero().toInt(), MONEY)
                else {
                    when (statementItem.recordType) {
                        StatementRecordType.SALE_BEER ->
                            onShowHistory?.invoke(
                                statementItem.recordId.orZero().toInt(),
                                BARREL_DELIVERY
                            )

                        StatementRecordType.SALE_BOTTLE ->
                            onShowHistory?.invoke(
                                statementItem.recordId.orZero().toInt(),
                                BOTTLE_DELIVERY
                            )

                        else -> {}
                    }
                }
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    private fun confirmDeleteStatement(statementModel: FinanceStatementUiModel) {
        requireContext().showAskingDialog(
            null,
            R.string.confirm_delete_text,
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            statementModel.recordId?.let {
                viewModel.deleteRecord(statementModel.recordType, it)
            }
        }
    }

    fun updateData() {
//        statementListAdapter.clearData()
        viewModel.requestStatementList()
    }

    inner class PaginatedListener(layoutManager: LinearLayoutManager) :
        PaginatedScrollListener(layoutManager) {

        override fun loadMoreItems() {
            viewModel.loadMoreData()
        }

        override fun isLastPage() = viewModel.isLastPage

        override fun isLoading() = viewModel.isDataLoading
    }

    companion object {
        const val TAG = "FinanceStatementFragment"

        fun newInstance(customerID: Int) = FinanceStatementFragment().apply {
            arguments = bundleOf(OBJ_ID to customerID)
        }
    }
}
