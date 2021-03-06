package com.example.beerdistrkt

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.fragPages.login.LoginFragment
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import java.text.SimpleDateFormat

abstract class BaseFragment<T : BaseViewModel> : Fragment() {

    protected abstract val viewModel: T
    protected val dateFormatDash = SimpleDateFormat("yyyy-MM-dd")

    fun showToast(message: String?) {
        if (!message.isNullOrEmpty())
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(strRes: Int) {
        showToast(getString(strRes))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.apiFailureLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.NoInternetConnection -> showToast(R.string.error_no_connection)
                is ApiResponseState.ApiError -> showToast(it.errorText)
            }
            if (it !is ApiResponseState.Sleep)
                viewModel.showNetworkFailMsgComplete()
        })
    }

    override fun onResume() {
        super.onResume()
        if (!Session.get().isUserLogged() && this !is LoginFragment)
            (activity as MainActivity).logOut()
    }
}