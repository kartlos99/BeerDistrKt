package com.example.beerdistrkt.fragPages.statement

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
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.KEY_HISTORY_OF
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.KEY_RECORD_ID
import com.example.beerdistrkt.paramViewModels
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StatementFragment : BaseFragment<StatementViewModel>() {

    override val viewModel by paramViewModels<StatementViewModel, StatementViewModel.Factory> { factory ->
        factory.create(clientID)
    }
    private val clientID by lazy {
        StatementFragmentArgs.fromBundle(requireArguments()).clientObjectID
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

        binding.chkGrAmonaweri.setOnCheckedChangeListener { _, isChecked ->
            pagesAdapter?.fragmentM?.changeStatementAppearance(isChecked)
            pagesAdapter?.fragmentK?.changeStatementAppearance(isChecked)
            if (pagesAdapter?.fragmentM?.action == null) {
                pagesAdapter?.fragmentM?.action = ::goEditing
                pagesAdapter?.fragmentK?.action = ::goEditing
                pagesAdapter?.fragmentM?.onShowHistory = ::showHistory
            }
            if (pagesAdapter?.fragmentM?.updateAnotherPage == null) {
                pagesAdapter?.fragmentM?.updateAnotherPage = {
                    showDebt()
                    pagesAdapter?.fragmentK?.updateData()
                }
                pagesAdapter?.fragmentK?.updateAnotherPage = {
                    showDebt()
                    pagesAdapter?.fragmentM?.updateData()
                }
            }
        }

        viewModel.clientLiveData.observe(viewLifecycleOwner) {
            binding.fragStatementClientInfo.text = it.name
        }
        showDebt()
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragStatementDebtContainer, debtFragment)
            .commit()
    }

    private fun showHistory(recordID: Int, historyOf: String) {
        val args = Bundle().apply {
            putInt(KEY_RECORD_ID, recordID)
            putString(KEY_HISTORY_OF, historyOf)
        }
        this.findNavController()
            .navigate(R.id.action_statementFragment_to_salesHistoryFragment, args)
    }

    private fun goEditing(operation: String, recordID: Int) {
        val action = StatementFragmentDirections
            .actionStatementFragmentToAddDeliveryFragment(clientID, operation, 0, recordID)
        this.findNavController().navigate(action)
    }

}
