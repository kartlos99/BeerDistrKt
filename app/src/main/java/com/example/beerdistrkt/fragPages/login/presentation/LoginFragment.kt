package com.example.beerdistrkt.fragPages.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.MainActViewModel
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.LoginFragmentBinding
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.show
import com.example.beerdistrkt.utils.visibleIf
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>() {

    private val binding by viewBinding(LoginFragmentBinding::bind)

    override val viewModel: LoginViewModel by viewModels()

    private lateinit var actViewModel: MainActViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actViewModel = (activity as MainActivity).viewModel
        initView()

        if (viewModel.session.isAccessTokenValid())
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        else
            checkSavedPass()

        observeData()
    }

    private fun getBuildInfo(): String {
        return if (BuildConfig.DEBUG) "${BuildConfig.BUILD_TYPE} ${BuildConfig.FLAVOR} ${BuildConfig.VERSION_NAME}"
        else BuildConfig.VERSION_NAME
    }

    private fun checkSavedPass() {
        val userName = SharedPreferenceDataSource.getInstance().getUserName()
        val password = SharedPreferenceDataSource.getInstance().getPass()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            with(binding) {
                viewLoginUserField.setText(userName)
                viewLoginPasswordField.setText(password)
                viewModel.signIn(userName, password)
                viewLoginLoginBtn.isEnabled = false
                viewLoginProgress.visibleIf(true)
            }
        }
    }

    private fun initView() = with(binding) {
        viewLoginLoginBtn.setOnClickListener {
            viewModel.signIn(
                viewLoginUserField.text.toString(),
                viewLoginPasswordField.text.toString()
            )
            viewLoginLoginBtn.isEnabled = false
            viewLoginProgress.show()
        }
        tvBuildInfo.text = getBuildInfo()
        if (BuildConfig.DEBUG) {
            viewLoginUserField.setText("kartlos")
            viewLoginPasswordField.setText("0000")
        }
    }

    private fun observeData() {
        viewModel.loginDataStateFlow.collectLatest(viewLifecycleOwner) { state ->
            binding.errorField.text = state.errorMessageRes?.let { getString(it) } ?: String.empty()
            binding.viewLoginProgress.isVisible = state.isLoading
            binding.viewLoginLoginBtn.isEnabled = state.isLoginEnabled
        }
        viewModel.openHomeScreenFlow.collectLatest(viewLifecycleOwner) {
            if (binding.viewLoginSaveChk.isChecked) {
                SharedPreferenceDataSource(requireContext()).saveUserName(
                    binding.viewLoginUserField.text.toString()
                )
                SharedPreferenceDataSource(requireContext()).savePassword(
                    binding.viewLoginPasswordField.text.toString()
                )
            }
            actViewModel.updateNavHeader()
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

}
