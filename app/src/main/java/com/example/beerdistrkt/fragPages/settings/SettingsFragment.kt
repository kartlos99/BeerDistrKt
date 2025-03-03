package com.example.beerdistrkt.fragPages.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.SettingsFragmentBinding
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingCode
import com.example.beerdistrkt.network.model.isLoading
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
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
                viewModel.setNewValue(SettingCode.IDLE_WARNING, newValue)
            }
                .show(childFragmentManager, EditValueDialog.TAG)

        }
        binding.setupObservers()
    }

    private fun SettingsFragmentBinding.setupObservers() {
        viewModel.apiStateFlow.collectLatest(viewLifecycleOwner) { result ->
            progressIndicator.isVisible = result.isLoading()
            result.onError { code, message ->
                showToast(message)
            }
            result.onSuccess {
                println(it)
                showToast(it.toString())
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

}