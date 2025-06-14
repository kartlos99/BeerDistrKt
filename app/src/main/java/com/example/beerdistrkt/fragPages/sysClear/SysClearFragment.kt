package com.example.beerdistrkt.fragPages.sysClear

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.SysClearFragmentBinding
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.sysClear.adapter.SysClearAdapter
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.SYS_CLEAR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SysClearFragment : BaseFragment<SysClearViewModel>() {

    private val binding by viewBinding(SysClearFragmentBinding::bind)

    override val viewModel by viewModels<SysClearViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sys_clear_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setFragmentResultListener(SYS_CLEAR_REQUEST_KEY, ::onResultReceived)
        setupMenu(R.menu.sys_clear_menu, ::onMenuItemSelected)
    }

    private fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.select_client -> {
                findNavController().navigate(
                    SysClearFragmentDirections.actionSysClearFragmentToObjListFragment(SYS_CLEAR)
                )
                return true
            }
        }
        return false
    }

    private fun onResultReceived(requestKey: String, bundle: Bundle) {
        if (requestKey == SYS_CLEAR_REQUEST_KEY)
            lifecycleScope.launch {
                viewModel.findClient(bundle.getInt(CLIENT_ID_KEY))?.let {
                    showConfirmDialog(it)
                } ?: showToast(R.string.some_error)
            }
    }

    private fun initViewModel() {
        viewModel.sysClearLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> initRecycler(it.data)
                else -> {}
            }
        }
        viewModel.addClearFlow.collectLatest(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(it.data)
                }

                else -> {}
            }
        }
    }

    private fun showConfirmDialog(client: Customer) =
        requireContext().showAskingDialog(
            getString(R.string.sys_clean),
            getString(R.string.confirm_sys_clear_on, client.name),
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            viewModel.addClearingData(client.id!!)
        }


    private fun initRecycler(data: List<SysClearModel>) = with(binding.sysClearRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = SysClearAdapter(data) { _, id ->
            requireContext().showAskingDialog(
                R.string.delete,
                R.string.confirm_delete_text,
                R.string.yes,
                R.string.no,
                R.style.ThemeOverlay_MaterialComponents_Dialog
            ) {
                viewModel.addClearingData(id, id)
            }
        }
    }

    companion object {
        const val SYS_CLEAR_REQUEST_KEY = "SYS_CLEAR_REQUEST_KEY"
        const val CLIENT_ID_KEY = "CLIENT_ID_KEY"
    }
}
