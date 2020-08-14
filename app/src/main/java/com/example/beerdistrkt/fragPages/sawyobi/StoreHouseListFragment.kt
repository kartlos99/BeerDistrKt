package com.example.beerdistrkt.fragPages.sawyobi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sawyobi.adapters.StoreHouseListAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.android.synthetic.main.sawyobi_list_fragment.*

class StoreHouseListFragment : BaseFragment<StoreHouseListViewModel>() {

    companion object {
        fun newInstance() = StoreHouseListFragment()
    }

    override val viewModel by lazy {
        getViewModel { StoreHouseListViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sawyobi_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    fun initViewModel() {
        viewModel.ioDoneLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {}
                is ApiResponseState.Success -> {
                    initIoList(it.data)
                }
            }
        })
    }

    private fun initIoList(dataList: List<IoModel>) {
        sHLRecycler.layoutManager = LinearLayoutManager(context)
        sHLRecycler.adapter = StoreHouseListAdapter(
            dataList.groupBy { it.ioDate },
            viewModel.beerMap
        )
    }
}
