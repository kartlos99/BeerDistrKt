package com.example.beerdistrkt.fragPages.showHistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.android.synthetic.main.fragment_show_history.*

class ShowHistoryFragment: BaseFragment<ShowHistoryViewModel>() {

    private val TAG = "ShowHistoryFragment"

    override val viewModel by lazy {
        getViewModel { ShowHistoryViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        val args = ShowHistoryFragmentArgs.fromBundle(arguments ?: Bundle())
        viewModel.getData(args.recordID)
        setPageTitle(R.string.order_history_title)
    }

    private fun initViewModel() {
        viewModel.orderHistoryLiveData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is ApiResponseState.Success -> showOrderHistory(it.data)
                else -> Log.d(TAG, "initViewModel: obz Ord Hist")
            }
        })
    }

    private fun showOrderHistory(data: List<OrderHistory>) {
        fragShowHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = HistoryAdapter(
            data,
            viewModel.beerMap
        )
        fragShowHistoryRc.adapter = adapter
    }
}