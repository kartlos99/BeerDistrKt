package com.example.beerdistrkt.fragPages.showHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.fragment_sales_history.*
import com.example.beerdistrkt.observe
import kotlinx.android.synthetic.main.fragment_show_history.*
import kotlinx.android.synthetic.main.view_sales_history_item.view.*

class SalesHistoryFragment: BaseFragment<SalesHistoryViewModel>() {
    override val viewModel by lazy {
        getViewModel { SalesHistoryViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sales_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
//        val args = ShowHistoryFragmentArgs.fromBundle(arguments ?: Bundle())
//        viewModel.getData(args.recordID)
        viewModel.getData(25787)
        setPageTitle(R.string.order_history_title)
    }

    private fun initViewModel() {
        viewModel.saleHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> fragSalesHistoryLoader.visibleIf(it.showLoading)
                is ApiResponseState.Success -> showHistory(it.data)
                else -> showToast(R.string.server_error)
            }
        }
    }

    private fun showHistory(data: List<SaleHistory>) {
        fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleListAdapter(
            data,
            null,
            R.layout.view_sales_history_item,
            onBind = {item, view ->
                view.viewSaleHistoryOperator.text = item.modifyUser.username
                view.viewSaleHistoryModifyDate.text = item.modifyDate
                view.viewSaleHistoryDistributor.text = item.distributor.username
                view.viewSaleHistoryAllBody.text = item.client.dasaxeleba + "\n" + item.beer.dasaxeleba + " " + item.canTypeID + " " + item.count + "\n" + item.comment

            }
        )
        fragSalesHistoryRc.adapter = adapter
    }

}

