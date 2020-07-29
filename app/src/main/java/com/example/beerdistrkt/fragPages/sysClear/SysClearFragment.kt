package com.example.beerdistrkt.fragPages.sysClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sysClear.adapter.SysClearAdapter
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.sys_clear_fragment.*

class SysClearFragment : BaseFragment<SysClearViewModel>() {

    override val viewModel by lazy {
        getViewModel { SysClearViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sys_clear_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.sys_clean)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.sysCleanLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {}
                is ApiResponseState.Success -> initRecycler(it.data)
            }
        })
    }

    private fun initRecycler(data: List<SysClearModel>) {
        sysClearRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = SysClearAdapter(data)
        adapter.onLongPress = {name, id ->
            showToast("$id : $name")
        }
        sysClearRecycler.adapter = adapter
    }
}
