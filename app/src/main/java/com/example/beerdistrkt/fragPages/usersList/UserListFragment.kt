package com.example.beerdistrkt.fragPages.usersList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.UserListFragmentBinding
import com.example.beerdistrkt.fragPages.usersList.adapter.UserAdapter
import com.example.beerdistrkt.models.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment : BaseFragment<UserListViewModel>() {

    companion object {
        fun newInstance() = UserListFragment()
    }

    private val binding by viewBinding(UserListFragmentBinding::bind)

    override val viewModel by viewModels<UserListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
            initUsersRecycler(users)
        }
    }

    private fun initUsersRecycler(users: List<User>) {
        binding.usersRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = UserAdapter(users)
        adapter.onClick = {
            findNavController().navigate(
                UserListFragmentDirections.actionUserListFragmentToAddUserFragment(it)
            )
        }
        binding.usersRecycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.user_list_manu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mAddUser -> {
                val direction = UserListFragmentDirections.actionUserListFragmentToAddUserFragment()
                findNavController().navigate(direction)
                return true
            }
        }
        return false
    }
}
