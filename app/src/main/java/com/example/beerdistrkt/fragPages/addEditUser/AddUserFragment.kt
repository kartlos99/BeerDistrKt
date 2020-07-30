package com.example.beerdistrkt.fragPages.addEditUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.goAway
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.add_user_fragment.*

class AddUserFragment : BaseFragment<AddUserViewModel>() {

    companion object {
        fun newInstance() = AddUserFragment()
    }

    override val viewModel by lazy {
        getViewModel { AddUserViewModel(userID) }
    }

    val userID by lazy {
        val args = AddUserFragmentArgs.fromBundle(arguments ?: Bundle())
        args.userID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        if (userID.isEmpty()) {
            addUserChangePassBox.goAway()
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.add_user)
        } else {
            addUserPass.goAway()
            addUserPassConfirm.goAway()
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.m_edit)
        }

        addUserChangePassBox.setOnCheckedChangeListener { _, isChecked ->
            addUserPass.visibleIf(isChecked)
            addUserPassConfirm.visibleIf(isChecked)
        }
    }

    private fun initViewModel() {
        viewModel.onDoneClick()
    }
}
