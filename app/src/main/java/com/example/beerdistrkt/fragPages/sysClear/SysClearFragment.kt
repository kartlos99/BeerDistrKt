package com.example.beerdistrkt.fragPages.sysClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sysClear.adapter.SysClearAdapter
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.sys_clear_fragment.*

class SysClearFragment : BaseFragment<SysClearViewModel>(), AdapterView.OnItemSelectedListener {

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

        sysClearModeBtn.setOnClickListener {
            onModeChange()
        }
        sysClearSaveBtn.setOnClickListener {
            viewModel.addClearingData()
        }
    }

    fun onModeChange() {
        viewModel.activeAdd = !viewModel.activeAdd
        sysClearSaveBtn.visibleIf(viewModel.activeAdd)
        sysCleanSpinner.visibleIf(viewModel.activeAdd)
        sysClearModeBtn.text = if (viewModel.activeAdd) "-" else "+"
    }

    private fun initViewModel() {
        viewModel.sysClearLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {
                }
                is ApiResponseState.Success -> initRecycler(it.data)
            }
        })
        viewModel.clientListLiveData.observe(viewLifecycleOwner, Observer { clientsList ->
            sysClearModeBtn.isEnabled = true

            val clientsAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_dropdown_item,
                clientsList.map { it.dasaxeleba }
            )
            sysCleanSpinner.adapter = clientsAdapter
            sysCleanSpinner.onItemSelectedListener = this
        })
        viewModel.addClearLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {
                }
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    if (viewModel.activeAdd)
                        onModeChange()
                }
            }
        })
    }

    private fun initRecycler(data: List<SysClearModel>) {
        sysClearRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = SysClearAdapter(data)
        adapter.onLongPress = { name, id ->
            requireContext().showAskingDialog(
                R.string.delete,
                R.string.confirm_delete_text,
                R.string.yes,
                R.string.no,
                R.style.ThemeOverlay_MaterialComponents_Dialog
            ) {
                viewModel.addClearingData(id)
            }
        }
        sysClearRecycler.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.selectClient(position)
    }
}
