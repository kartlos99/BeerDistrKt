package com.example.beerdistrkt.fragPages.statement.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.MyPagesAdapter
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.databinding.StatementFragmentBinding
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.utils.YES
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StatementFragment : BaseFragment<StatementViewModel>() {

    override val viewModel by paramViewModels<StatementViewModel, StatementViewModel.Factory> { factory ->
        factory.create(clientID)
    }
    private val clientID by lazy {
        StatementFragmentArgs.fromBundle(requireArguments()).clientObjectID
    }
    private val needUpdate by lazy {
        StatementFragmentArgs.fromBundle(requireArguments()).needUpdate
    }

    private val binding by viewBinding(StatementFragmentBinding::bind)

    private var pagesAdapter: MyPagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.statement_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagesAdapter = MyPagesAdapter(childFragmentManager, clientID)
        binding.statementViewpager.adapter = pagesAdapter
        binding.tabsAmonaweri.setupWithViewPager(binding.statementViewpager)

        viewModel.clientLiveData.observe(viewLifecycleOwner) {
            binding.fragStatementClientInfo.text = it.name
        }
        showDebt()
        if (needUpdate == YES) updateSales()
    }

    fun updateDebt() {
        (childFragmentManager.findFragmentById(R.id.fragStatementDebtContainer) as? ClientDebtFragment)?.refreshData()
    }

    fun updateBarrels() {
        pagesAdapter?.fragmentK?.updateData()
    }

    fun updateSales() {
        pagesAdapter?.fragmentM?.updateData()
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragStatementDebtContainer, debtFragment)
            .commit()
    }

    /*private fun showHistory(recordID: Int, historyOf: String) {
        this.findNavController().navigate(
            StatementFragmentDirections.actionStatementFragmentToSalesHistoryFragment(
                recordID,
                historyOf,
            )
        )
    }

    private fun editingFinanceStatement(operation: String, recordID: Long) {
        val action = StatementFragmentDirections
            .actionStatementFragmentToAddDeliveryFragment(clientID, operation, 0, recordID.toInt())
        this.findNavController().navigate(action)
    }

    private fun goEditing(operation: String, recordID: Int) {
        val action = StatementFragmentDirections
            .actionStatementFragmentToAddDeliveryFragment(clientID, operation, 0, recordID)
        this.findNavController().navigate(action)
    }*/

}
