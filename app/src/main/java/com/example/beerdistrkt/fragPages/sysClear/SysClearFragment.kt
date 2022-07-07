package com.example.beerdistrkt.fragPages.sysClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.FragmentSalesHistoryBinding
import com.example.beerdistrkt.databinding.SysClearFragmentBinding
import com.example.beerdistrkt.fragPages.sysClear.adapter.SysClearAdapter
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.visibleIf

class SysClearFragment : BaseFragment<SysClearViewModel>(), AdapterView.OnItemSelectedListener {

    private val binding by viewBinding(SysClearFragmentBinding::bind)

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
        initViewModel()

        binding.sysClearModeBtn.setOnClickListener {
            onModeChange()
        }
        binding.sysClearSaveBtn.setOnClickListener {
            viewModel.addClearingData()
        }
    }

    private fun onModeChange() {
        viewModel.activeAdd = !viewModel.activeAdd
        with(binding) {
            sysClearSaveBtn.visibleIf(viewModel.activeAdd)
            sysCleanSpinner.visibleIf(viewModel.activeAdd)
            sysClearModeBtn.text = if (viewModel.activeAdd) "-" else "+"
        }
    }

    private fun initViewModel() {
        viewModel.sysClearLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {
                }
                is ApiResponseState.Success -> initRecycler(it.data)
                else -> {}
            }
        })
        viewModel.clientListLiveData.observe(viewLifecycleOwner, Observer { clientsList ->
            with(binding) {
                sysClearModeBtn.isEnabled = true

                val clientsAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_dropdown_item,
                    clientsList.map { it.dasaxeleba }
                )
                sysCleanSpinner.adapter = clientsAdapter
                sysCleanSpinner.onItemSelectedListener = this@SysClearFragment
            }
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
                else -> {}
            }
        })
    }

    private fun initRecycler(data: List<SysClearModel>) {
        binding.sysClearRecycler.layoutManager = LinearLayoutManager(context)
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
        binding.sysClearRecycler.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.selectClient(position)
    }
}
