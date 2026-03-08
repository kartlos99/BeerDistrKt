package com.example.beerdistrkt.fragPages.statement.presentation.barrels

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.PaginatedScrollListener
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.StatementSubPageFragmentBinding
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelIo
import com.example.beerdistrkt.fragPages.statement.presentation.StatementFragment
import com.example.beerdistrkt.fragPages.statement.presentation.StatementFragmentDirections
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.BarrelsIoViewModel.*
import com.example.beerdistrkt.fragPages.statement.presentation.barrels.adapter.BarrelsStatementAdapter
import com.example.beerdistrkt.fragPages.statement.presentation.dialog.StatementOption
import com.example.beerdistrkt.fragPages.statement.presentation.dialog.StatementOptionsDialog
import com.example.beerdistrkt.fragPages.statement.presentation.dialog.StatementOptionsDialog.Companion.ACTION_KEY
import com.example.beerdistrkt.fragPages.statement.presentation.dialog.StatementOptionsDialog.Companion.OPTIONS_REQUEST_KEY
import com.example.beerdistrkt.getParcelableObject
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.orZero
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.K_OUT
import com.example.beerdistrkt.utils.OBJ_ID
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BarrelsIoFragment : BaseFragment<BarrelsIoViewModel>() {

    private val binding by viewBinding(StatementSubPageFragmentBinding::bind)

    private lateinit var barrelsAdapter: BarrelsStatementAdapter

    private val clientID by lazy {
        arguments?.getInt(OBJ_ID).orZero()
    }

    override val viewModel by paramViewModels<BarrelsIoViewModel, Factory> { factory ->
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

//        BottomSheetDialog()
        setResultListener()
    }


    private fun setResultListener() {
        childFragmentManager.setFragmentResultListener(
            OPTIONS_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            bundle.getParcelableObject<StatementOption>(ACTION_KEY)?.let { selectedAction ->
                viewModel.onActionSelected(selectedAction)
            }
        }
    }

    private fun initView() = with(binding) {

    }

    private fun initRecycler() = with(binding.statementSubPageRc) {
        val linearLayoutManager = LinearLayoutManager(context)
        barrelsAdapter = BarrelsStatementAdapter(viewModel)
        layoutManager = linearLayoutManager
        this.adapter = barrelsAdapter
        addOnScrollListener(PaginatedListener(linearLayoutManager))
    }

    private fun observeData() {
//        viewModel.isGroupedLiveData.observe(viewLifecycleOwner) { grouped: Boolean ->
//            statementListAdapter.isGrouped = grouped
//        }
        viewModel.statementLiveData.observe(viewLifecycleOwner) { result ->
            binding.statementProgressBar.isVisible = result is ResultState.Loading
            result.onSuccess {
                barrelsAdapter.submitList(it)
            }
        }
        viewModel.apiState.collectLatest(viewLifecycleOwner) {
            binding.statementProgressBar.isVisible = it is ResultState.Loading
        }
        viewModel.eventsFlow.collectLatest(viewLifecycleOwner) { event ->
            when (event) {
                UiEvent.CantModify -> showToast(R.string.no_edit_access)
                UiEvent.CantModifyInputs -> showToast(R.string.cant_modify_input_in_barrels_io)
                is UiEvent.DeleteConfirmation -> confirmDeleteStatement(event.recordId)
                is UiEvent.GoEdit -> {
                    val action = StatementFragmentDirections
                        .actionStatementFragmentToAddDeliveryFragment(
                            clientObjectID = clientID,
                            operacia = event.typeAndId.first,
                            orderID = 0,
                            recordID = event.typeAndId.second.toInt()
                        )
                    parentFragment?.findNavController()?.navigate(action)
                }

                UiEvent.OpenOptions ->
                    StatementOptionsDialog.newInstance(K_OUT)
                        .show(childFragmentManager, StatementOptionsDialog.TAG)

                is UiEvent.SelectModifyingOutput -> {
                    showBarrelOutputSelectorDialog(event.items) {
                        viewModel.onModifyingOutputSelected(it)
                    }
                }

                UiEvent.ShowDeleteSucceed -> {
                    showToast(R.string.is_deleted)
                    (parentFragment as? StatementFragment)?.updateDebt()
                }

                is UiEvent.ShowError -> showToast(event.msg)
            }
            /*when (it) {
                FinanceStatementViewModel.UiEvent.CantModify ->
                    showToast(R.string.no_edit_access)

                FinanceStatementViewModel.UiEvent.OpenOptions ->
                    StatementOptionsDialog().show(childFragmentManager, StatementOptionsDialog.TAG)

                is FinanceStatementViewModel.UiEvent.GoEdit -> {
                    val action = StatementFragmentDirections
                        .actionStatementFragmentToAddDeliveryFragment(
                            clientObjectID = clientID,
                            operacia = it.typeAndId.first,
                            orderID = 0,
                            recordID = it.typeAndId.second.toInt()
                        )
                    parentFragment?.findNavController()?.navigate(action)
                }

                is FinanceStatementViewModel.UiEvent.GoHistory -> {
                    val action = StatementFragmentDirections
                        .actionStatementFragmentToSalesHistoryFragment(
                            historyOf = it.subjectAndId.first,
                            recordID = it.subjectAndId.second.toInt()
                        )
                    parentFragment?.findNavController()?.navigate(action)
                }

                is FinanceStatementViewModel.UiEvent.DeleteConfirmation ->
                    confirmDeleteStatement(it.tableAndId)

                FinanceStatementViewModel.UiEvent.ShowDeleteSucceed -> {
                    showToast(R.string.is_deleted)
                    (parentFragment as? StatementFragment)?.updateDebt()
                }

                FinanceStatementViewModel.UiEvent.ShowDeleteSucceedWithUpdateRequest -> {
                    showToast(R.string.is_deleted)
                    (parentFragment as? StatementFragment)?.updateDebt()
                    (parentFragment as? StatementFragment)?.updateBarrels()
                }

                is FinanceStatementViewModel.UiEvent.ShowError -> showToast(it.msg)
            }*/
        }
    }

    private fun showBarrelOutputSelectorDialog(
        barrelOutputs: List<BarrelIo>,
        onComplete: (selectedOutput: BarrelIo?) -> Unit
    ) {
        var selectedOutput: BarrelIo? = null
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.select_barrel_io_dialog_title))
            .setCancelable(true)
            .setSingleChoiceItems(
                barrelOutputs.map { "${it.barrel.displayName} x ${it.countOut}" }.toTypedArray(),
                -1,
            ) { _, i ->
                selectedOutput = barrelOutputs[i]
            }
            .setPositiveButton(R.string.ok) { _, _ -> }

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (selectedOutput == null) {
                showToast(R.string.no_selection)
            }
            onComplete.invoke(selectedOutput)
            alertDialog.dismiss()
        }
    }

    fun changeStatementAppearance(grouped: Boolean) {
//        viewModel.changeDataStructure(grouped)
    }

    /*
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
    */

    private fun confirmDeleteStatement(data: Long) {
        requireContext().showAskingDialog(
            null,
            R.string.confirm_delete_text,
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            viewModel.deleteRecord(data)
        }
    }

    fun updateData() {
//        statementListAdapter.clearData()
        viewModel.requestStatementList()
    }

    inner class PaginatedListener(val layoutManager: LinearLayoutManager) :
        PaginatedScrollListener(layoutManager) {

        override fun loadMoreItems() {
            viewModel.loadMoreData()
        }

        override fun isLastPage() = viewModel.isLastPage

        override fun isLoading() = viewModel.isDataLoading
    }

    companion object {
        const val TAG = "FinanceStatementFragment"

        fun newInstance(customerID: Int) = BarrelsIoFragment().apply {
            arguments = bundleOf(OBJ_ID to customerID)
        }
    }
}
