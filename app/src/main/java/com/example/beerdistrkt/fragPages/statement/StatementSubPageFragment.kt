package com.example.beerdistrkt.fragPages.statement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.PaginatedScrollListener
import com.example.beerdistrkt.databinding.StatementSubPageFragmentBinding
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BARREL_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BOTTLE_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.MONEY
import com.example.beerdistrkt.fragPages.statement.adapter.StatementAdapter
import com.example.beerdistrkt.fragPages.statement.model.CtxMenuItem
import com.example.beerdistrkt.fragPages.statement.model.StatementModel
import com.example.beerdistrkt.fragPages.statement.model.StatementRecordType
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.LOCATION
import com.example.beerdistrkt.utils.OBJ_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatementSubPageFragment : BaseFragment<StatementSubPageViewModel>() {

    private val vBinding by viewBinding(StatementSubPageFragmentBinding::bind)

    private var pagePos: Int = -1

    var action: ((operation: String, recordID: Int) -> Unit)? = null
    var updateAnotherPage: (() -> Unit)? = null
    var onShowHistory: ((recordID: Int, historyOf: String) -> Unit)? = null
    private lateinit var statementListAdapter: StatementAdapter

    override val viewModel by viewModels<StatementSubPageViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        pagePos = arguments?.getInt(LOCATION) ?: 0
        viewModel.pagePos = pagePos
        viewModel.clientID = arguments?.getInt(OBJ_ID) ?: 0

        return inflater.inflate(R.layout.statement_sub_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBinding.amoColumnTitle1.text = getString(R.string.text_tarigi)
        if (pagePos == 0) {
            vBinding.amoColumnTitle2.text = getString(R.string.price)
            vBinding.amoColumnTitle3.text = getString(R.string.pay)
            vBinding.amoColumnTitle4.text = getString(R.string.davalianeba)
        } else {
            vBinding.amoColumnTitle2.text = getString(R.string.mitanili_kasrebi)
            vBinding.amoColumnTitle3.text = getString(R.string.wamogebuli_kasrebi)
            vBinding.amoColumnTitle4.text = getString(R.string.nashti_at_client)
        }

        val linearLayoutManager = LinearLayoutManager(context)
        statementListAdapter = StatementAdapter(mutableListOf(), pagePos)

        vBinding.statementSubPageRc.apply {
            layoutManager = linearLayoutManager
            adapter = statementListAdapter
            addOnScrollListener(PaginatedListener(linearLayoutManager))
        }
        viewModel.requestStatementList()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.isGroupedLiveData.observe(viewLifecycleOwner) { grouped: Boolean ->
            statementListAdapter.isGrouped = grouped
        }
        viewModel.statementLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading ->
                    vBinding.statementProgressBar.isVisible = it.showLoading

                is ApiResponseState.Success -> {
                    statementListAdapter.addItems(it.data)
                }

                else -> {}
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
                val statementItem = statementListAdapter.getItem(item.groupId)
                action?.invoke(statementItem.getType(pagePos), statementItem.id)
                true
            } else false

            CtxMenuItem.EditBarrel.itemID -> if (pagePos == 1) {
                val statementItem = statementListAdapter.getItem(item.groupId)
                action?.invoke(statementItem.getType(pagePos), statementItem.id)
                true
            } else false

            CtxMenuItem.Delete.itemID -> if (pagePos == 0) {
                confirmDeleteStatement(statementListAdapter.getItem(item.groupId))
                true
            } else false

            CtxMenuItem.DeleteBarrel.itemID -> if (pagePos == 1) {
                confirmDeleteStatement(statementListAdapter.getItem(item.groupId))
                true
            } else false

            CtxMenuItem.History.itemID -> {
                val statementItem = statementListAdapter.getItem(item.groupId)
                if (statementItem.pay != 0F)
                    onShowHistory?.invoke(statementItem.id, MONEY)
                else {
                    when (statementItem.recordType) {
                        StatementRecordType.SALE_BEER ->
                            onShowHistory?.invoke(statementItem.id, BARREL_DELIVERY)

                        StatementRecordType.SALE_BOTTLE ->
                            onShowHistory?.invoke(statementItem.id, BOTTLE_DELIVERY)

                        else -> {}
                    }
                }
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    private fun confirmDeleteStatement(statementModel: StatementModel) {
        requireContext().showAskingDialog(
            null,
            R.string.confirm_delete_text,
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            val tableName = statementModel.getType(pagePos)
            viewModel.deleteRecord(tableName, statementModel.id)
            statementListAdapter.clearData()
        }
    }

    fun updateData() {
        statementListAdapter.clearData()
        viewModel.requestStatementList()
    }

    inner class PaginatedListener(layoutManager: LinearLayoutManager) :
        PaginatedScrollListener(layoutManager) {

        override fun loadMoreItems() {
            viewModel.loadMoreData()
        }

        override fun isLastPage() = viewModel.isLastPage

        override fun isLoading() = viewModel.isLoading
    }

    companion object {
        fun newInstance() = StatementSubPageFragment()
        const val TAG = "StatementSubPageFragment"
    }
}
