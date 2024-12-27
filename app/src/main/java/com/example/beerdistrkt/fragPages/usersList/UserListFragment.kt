package com.example.beerdistrkt.fragPages.usersList

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.UserListFragmentBinding
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.usersList.adapter.UserAdapter
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.isLoading
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment : BaseFragment<UserListViewModel>() {

    companion object {
        fun newInstance() = UserListFragment()
    }

    private val binding by viewBinding(UserListFragmentBinding::bind)

    override val viewModel by viewModels<UserListViewModel>()

    override var frLayout: Int? = R.layout.user_list_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        addBtn.setOnClickListener {
            openDetailsPage()
        }
        swipeRefresh.setOnRefreshListener {
            viewModel.refreshUsers()
        }
    }

    private fun initViewModel() {
        viewModel.usersStateFlow.collectLatest(viewLifecycleOwner, action = ::handleUsers)
    }

    private fun handleUsers(result: ResultState<List<User>>) = with(binding) {
        progressIndicator.isVisible = result.isLoading()
        result.onSuccess { users ->
            initUsersRecycler(users)
            binding.swipeRefresh.isRefreshing = false
        }
        result.onError { code, message ->
            showToast(message)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun initUsersRecycler(users: List<User>) {
        binding.usersRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = UserAdapter(users)
        adapter.onClick = ::openDetailsPage
        binding.usersRecycler.adapter = adapter
    }

    private fun openDetailsPage(userID: String? = null) {
        findNavController().navigate(
            UserListFragmentDirections.actionUserListFragmentToAddUserFragment(userID.orEmpty())
        )
    }
}
