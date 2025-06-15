package com.example.beerdistrkt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.fragPages.login.presentation.LoginFragment
import com.example.beerdistrkt.fragPages.statement.StatementSubPageFragment
import com.example.beerdistrkt.utils.ApiResponseState
import java.text.SimpleDateFormat
import java.util.Locale

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    protected abstract val viewModel: VM
    protected val dateFormatDash = SimpleDateFormat(SIMPLE_DATE_PATTERN, Locale.getDefault())

    fun showToast(message: String?) {
        if (!message.isNullOrEmpty())
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(strRes: Int) {
        showToast(getString(strRes))
    }

    @LayoutRes
    open var frLayout: Int? = null

    @StringRes
    open val titleRes: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (frLayout != null)
            inflater.inflate(frLayout!!, container, false)
        else
            super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onResume() {
        super.onResume()
        titleRes?.let { setPageTitle(it) }
        if (
            this !is LoginFragment
            && this !is StatementSubPageFragment // because it's placed on another fragment
        )
            viewModel.checkToken()
    }

    private fun navigateOut() = findNavController().run {
        navigate(R.id.action_global_loginFragment)
        clearBackStack(R.id.loginFragment)
    }

    fun setPageTitle(title: String?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    fun setPageTitle(titleRes: Int) = setPageTitle(getString(titleRes))

    fun isAccessTokenValid() = viewModel.session.isAccessTokenValid()

    fun setupMenu(@MenuRes menuRes: Int, onItemClick: (menuItem: MenuItem) -> Boolean) {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(menuRes, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return onItemClick.invoke(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeData() = with(viewModel) {
        uiEventFlow.collectLatest(viewLifecycleOwner) { event ->
            when (event) {
                BaseViewModel.UiEvent.LogOut -> navigateOut()
            }
        }
        apiFailureLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.NoInternetConnection -> showToast(R.string.error_no_connection)
                is ApiResponseState.ApiError -> {
                    showToast(it.errorText)
                    if (it.errorCode == 401) {
                        viewModel.forceLogout()
                    }
                }

                else -> {}
            }
            if (it !is ApiResponseState.Sleep)
                viewModel.showNetworkFailMsgComplete()
        }
    }

    companion object {
        const val TAG = "TAG_FR"
        const val SIMPLE_DATE_PATTERN = "yyyy-MM-dd"
        const val AUTO_BACK_DELAY = 1234L
    }
}