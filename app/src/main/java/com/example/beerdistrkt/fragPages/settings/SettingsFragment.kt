package com.example.beerdistrkt.fragPages.settings

import android.os.Bundle
import android.view.View
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.SettingsFragmentBinding
import com.example.beerdistrkt.fragPages.settings.SettingsViewModel.SettingCode.IDLE_WARNING
import com.example.beerdistrkt.getViewModel

class SettingsFragment : BaseFragment<SettingsViewModel>() {
    override val viewModel by lazy {
        getViewModel { SettingsViewModel() }
    }

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
    }

    fun SettingsFragmentBinding.setupObservers() {
//        viewModel.
    }

}