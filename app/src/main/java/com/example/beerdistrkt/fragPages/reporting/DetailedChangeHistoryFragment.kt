package com.example.beerdistrkt.fragPages.reporting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentDetailedChangeHinstoryBinding
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName

class DetailedChangeHistoryFragment : BaseFragment<DetailedChangeHistoryViewModel>() {

    override val viewModel: DetailedChangeHistoryViewModel by viewModels()

    val binding by viewBinding(FragmentDetailedChangeHinstoryBinding::bind)

    override var frLayout: Int? = R.layout.fragment_detailed_change_hinstory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setPageTitle(R.string.sale_history_title)

        arguments?.let {
            val id = it.getString(RECORD_ID_KEY) ?: return@let
            val table = it.getString(DB_TABLE_KEY) ?: return@let
            setItem(id, table)
        }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.historyFlow.collectLatest(viewLifecycleOwner) { history ->
//            changesAdapter.submitList(chList)
            binding.details.text = history.toString()
        }
    }

    fun setItem(id: String, table: String) {
        viewModel.getHistory(id, DbTableName.valueOf(table))
    }

    companion object {
        const val RECORD_ID_KEY = "RECORD_ID_KEY"
        const val DB_TABLE_KEY = "DB_TABLE_KEY"
    }
}