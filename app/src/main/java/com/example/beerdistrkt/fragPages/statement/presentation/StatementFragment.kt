package com.example.beerdistrkt.fragPages.statement.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.StatementPagesAdapter
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.databinding.StatementFragmentBinding
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.utils.K_OUT
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StatementFragment : BaseFragment<StatementViewModel>() {

    override val viewModel by paramViewModels<StatementViewModel, StatementViewModel.Factory> { factory ->
        factory.create(clientID)
    }
    private val clientID by lazy {
        StatementFragmentArgs.fromBundle(requireArguments()).clientObjectID
    }
    private val operation by lazy {
        StatementFragmentArgs.fromBundle(requireArguments()).operation
    }

    private val binding by viewBinding(StatementFragmentBinding::bind)

    private var pagesAdapter: StatementPagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.statement_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagesAdapter =
            StatementPagesAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, clientID)
        binding.statementViewpager.adapter = pagesAdapter
        TabLayoutMediator(binding.tabsAmonaweri, binding.statementViewpager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.finances)
                1 -> getString(R.string.barrels)
                else -> String.empty()
            }
        }.attach()

        viewModel.clientLiveData.observe(viewLifecycleOwner) {
            binding.fragStatementClientInfo.text = it.name
        }
        showDebt()
        if (operation == K_OUT) selectBarrelsPage()
    }

    private fun selectBarrelsPage() {
        binding.tabsAmonaweri.selectTab(binding.tabsAmonaweri.getTabAt(1))
        binding.statementViewpager.setCurrentItem(1, false)
    }

    fun updateDebt() {
        (childFragmentManager.findFragmentById(R.id.fragStatementDebtContainer) as? ClientDebtFragment)?.refreshData()
    }

    fun updateBarrels() {
        pagesAdapter?.getBarrelStatementFragment()?.updateData()
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragStatementDebtContainer, debtFragment)
            .commit()
    }

}
