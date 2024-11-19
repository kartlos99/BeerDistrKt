package com.example.beerdistrkt.fragPages.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.SettingsFragmentBinding
import com.example.beerdistrkt.fragPages.settings.SettingsViewModel.SettingCode.IDLE_WARNING
import com.example.beerdistrkt.fragPages.settings.model.SettingParam
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel>() {

    override val viewModel by viewModels<SettingsViewModel>()

    override var frLayout: Int? = R.layout.settings_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPageTitle(R.string.settings)
        val binding = SettingsFragmentBinding.bind(view)
        binding.idleSettingsValue.setOnClickListener {
            EditValueDialog(
                getString(R.string.new_value),
                binding.idleSettingsValue.text.toString()
            ) { newValue ->
                viewModel.setNewValue(IDLE_WARNING, newValue)
            }
                .show(childFragmentManager, EditValueDialog.TAG)

        }
        binding.setupObservers()
    }

    private fun SettingsFragmentBinding.setupObservers() {
        viewModel.getSettingsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> proceedData(it.data)
                is ApiResponseState.Loading -> progressIndicator.isVisible = it.showLoading
                is ApiResponseState.ApiError -> showToast(it.errorText)
                else -> {}
            }
        }
        viewModel.updateValuesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    idleSettingsValue.text = it.data
                }
                is ApiResponseState.Loading -> progressIndicator.isVisible = it.showLoading
                is ApiResponseState.ApiError -> showToast(it.errorText)
                else -> {}
            }
        }
    }

    private fun SettingsFragmentBinding.proceedData(data: List<SettingParam>) {
        idleSettingsValue.text = data.firstOrNull {
            it.code == IDLE_WARNING.code
        }?.valueInt.toString()
    }

}