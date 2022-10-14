package com.example.beerdistrkt

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.beerdistrkt.fragPages.amonaweri.AmonaweriSubPageFrag
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apiFailureLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.NoInternetConnection -> showToast(R.string.error_no_connection)
                is ApiResponseState.ApiError -> {
                    showToast(it.errorText)
                    if (it.errorCode == 401) {
                        automatedLogout()
                    }
                }
                else -> {}
            }
            if (it !is ApiResponseState.Sleep)
                viewModel.showNetworkFailMsgComplete()
        })
    }

    override fun onResume() {
        super.onResume()
        if (
            !Session.get().isAccessTokenValid()
            && this !is LoginFragment
            && this !is AmonaweriSubPageFrag // because it's placed on another fragment
        )
            automatedLogout()
    }

    private fun automatedLogout() {
        (activity as MainActivity).logOut()
    }

    fun setPageTitle(title: String?) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    fun setPageTitle(titleRes: Int) = setPageTitle(getString(titleRes))

    fun isAccessTokenValid() = Session.get().isAccessTokenValid()
}