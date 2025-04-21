package com.example.beerdistrkt.fragPages.showHistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentShowHistoryBinding
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowHistoryFragment : BaseFragment<ShowHistoryViewModel>() {

    private val binding by viewBinding(FragmentShowHistoryBinding::bind)

    override val viewModel: ShowHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        val args = ShowHistoryFragmentArgs.fromBundle(arguments ?: Bundle())
        viewModel.getData(args.recordID)
        setPageTitle(R.string.order_history_title)
    }

    private fun setObservers() = with(viewModel) {
        orderHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> showOrderHistory(it.data)
                else -> Log.d(TAG, "initViewModel: obz Ord Hist")
            }
        }
        infoEventsFlow.collectLatest(viewLifecycleOwner) { infoMessage ->
            showToast(infoMessage)
        }
    }

    private fun showOrderHistory(data: List<OrderHistory>) {
        binding.fragShowHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = HistoryAdapter(
            data,
            viewModel.beerMap
        )
        binding.fragShowHistoryRc.adapter = adapter
    }

    companion object {
        private const val TAG = "ShowHistoryFragment"
    }
}