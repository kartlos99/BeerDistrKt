package com.example.beerdistrkt.fragPages.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.adapter.SimpleDataAdapter
import com.example.beerdistrkt.databinding.SettingRowItemBinding
import com.example.beerdistrkt.databinding.SettingsFragmentBinding
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingParam
import com.example.beerdistrkt.network.model.isLoading
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val viewModel by viewModels<SettingsViewModel>()

    override var frLayout: Int? = R.layout.settings_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SettingsFragmentBinding.bind(view).apply {
            initRecycler()
        }
    }

    private fun SettingsFragmentBinding.initRecycler() {
        val settingsAdapter = SimpleDataAdapter<SettingParam>(
            layoutId = R.layout.setting_row_item,
            onBind = { item, view ->
                SettingRowItemBinding.bind(view).apply {
                    settingTitle.text = getString(item.code.displayName)
                    item.value.toString().also { settingValue.text = it }
                    settingValue.setOnClickListener {
                        openValueChangeDialog(item)
                    }
                }
            }
        )
        recycler.adapter = settingsAdapter

        viewModel.apiStateFlow.collectLatest(viewLifecycleOwner) { result ->
            progressIndicator.isVisible = result.isLoading()
            result.onError { code, message ->
                showToast(message)
            }
            result.onSuccess {
                settingsAdapter.submitList(it)
                println(it)
            }
        }
    }

    private fun openValueChangeDialog(item: SettingParam) {
        EditValueDialog(
            getString(R.string.new_value),
            item.value.toString()
        ) { newValue ->
            viewModel.setNewValue(item, newValue)
        }
            .show(childFragmentManager, EditValueDialog.TAG)
    }

}