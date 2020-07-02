package com.example.beerdistrkt.fragPages.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.BaseFragment

import com.example.beerdistrkt.R
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : BaseFragment<LoginViewModel>() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override val viewModel by lazy {
        getViewModel { LoginViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLoginLoginBtn.setOnClickListener {
            viewModel.logIn(
                viewLoginUserField.text.toString(),
                viewLoginPasswordField.text.toString()
            )
        }

        if (Session.get().isUserLogged()) {
            Session.get().clearSession()
            SharedPreferenceDataSource(requireContext()).saveUserName("")
            SharedPreferenceDataSource(requireContext()).savePassword("")
        } else
            checkSavedPass()

        initViewModel()
    }

    private fun checkSavedPass() {
        val userName = SharedPreferenceDataSource(requireContext()).getUserName()
        val password = SharedPreferenceDataSource(requireContext()).getPass()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            viewLoginUserField.setText(userName)
            viewLoginPasswordField.setText(password)
            viewModel.logIn(userName, password)
        }
    }

    private fun initViewModel() {
        viewModel.loginResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast("login aq")
                    if (viewLoginSaveChk.isChecked) {
                        SharedPreferenceDataSource(requireContext()).saveUserName(it.data.username)
                        SharedPreferenceDataSource(requireContext()).savePassword(
                            viewLoginPasswordField.text.toString()
                        )
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    viewModel.loginResponseLiveData.value = ApiResponseState.Sleep
                }
            }
        })
    }

}
