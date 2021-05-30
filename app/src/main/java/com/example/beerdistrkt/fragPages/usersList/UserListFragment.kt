package com.example.beerdistrkt.fragPages.usersList

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.usersList.adapter.UserAdapter
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.User
import kotlinx.android.synthetic.main.user_list_fragment.*

class UserListFragment : BaseFragment<UserListViewModel>() {

    companion object {
        fun newInstance() = UserListFragment()
    }

    override val viewModel by lazy {
        getViewModel { UserListViewModel() }
    }

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
        viewModel.usersLiveData.observe(viewLifecycleOwner, Observer {
            initUsersRecycler(it)
        })
    }

    private fun initUsersRecycler(users: List<User>) {
        usersRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = UserAdapter(users)
        adapter.onClick = {
            val direction = UserListFragmentDirections.actionUserListFragmentToAddUserFragment()
            direction.userID = it
            findNavController().navigate(direction)
        }
        usersRecycler.adapter = adapter
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
