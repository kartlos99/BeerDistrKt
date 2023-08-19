package com.example.beerdistrkt.fragPages.reporting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.FragmentDetailedChangeHinstoryBinding
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment

class DetailedChangeHistoryFragment : BaseFragment<DetailedChangeHistoryViewModel>() {
    override val viewModel: DetailedChangeHistoryViewModel by viewModels()

    val binding by viewBinding(
        FragmentDetailedChangeHinstoryBinding::bind
    )

    override var frLayout: Int? = R.layout.fragment_detailed_change_hinstory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setPageTitle(R.string.sale_history_title)

        arguments?.let {
            val id = it.getString(RECORD_ID_KEY)
            binding.details.text = id
        }
    }

    fun setItem(id: String) {
        binding.details.text = id
    }

    companion object {
        const val RECORD_ID_KEY = "RECORD_ID_KEY"
    }
}