package com.example.beerdistrkt.fragPages.addEditUser

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.addEditUser.models.AddUserRequestModel
import com.example.beerdistrkt.fragPages.usersList.UserListFragmentDirections
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
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
        if (userID.isNotEmpty())
            setHasOptionsMenu(true)
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

        addUserDoneBtn.setOnClickListener {
            if (isFormValid()) {
                val user = readUser()
                val requestModel = AddUserRequestModel(
                    user,
                    addUserPass.editText?.text.toString(),
                    addUserChangePassBox.isChecked
                )
                viewModel.onDoneClick(requestModel)
            } else {
                showToast(R.string.enter_corect_data)
            }
        }
    }

    private fun isFormValid(): Boolean {
        return if (userID.isNotEmpty() && !addUserChangePassBox.isChecked)
            validateUsername() and validateName()
        else
            validateUsername() and validateName() and validatePassword() and validateConfirmPass()
    }

    private fun readUser(): User {
        return User(
            userID,
            addUserUsername.editText?.text.toString(),
            addUserName.editText?.text.toString(),
            if (addUserAdminBox.isChecked) "2" else "1",
            addUserPhone.editText?.text.toString(),
            addUserAddress.editText?.text.toString(),
            Session.get().userID ?: "0",
            addUserComment.editText?.text.toString()
        )
    }

    private fun initViewModel() {
        viewModel.addUserLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {
                }
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    findNavController().navigateUp()
                }
            }
        })
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer { user ->
            Log.d("user", user.toString())
            if (user != null) {
                fillForm(user)
            }
        })
        viewModel.deleteUserLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(R.string.deleted)
                    findNavController().navigateUp()
                }
            }
        })
    }

    private fun fillForm(user: User) {
        addUserUsername.editText?.setText(user.username)
        addUserName.editText?.setText(user.name)
        addUserAdminBox.isChecked = user.type == "2"
        addUserPhone.editText?.setText(user.tel)
        addUserAddress.editText?.setText(user.adress)
        addUserComment.editText?.setText(user.comment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_user_manu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mDeleteUser -> {
                requireContext().showAskingDialog(
                    R.string.delete,
                    R.string.confirm_delete_text,
                    R.string.yes,
                    R.string.no,
                    R.style.ThemeOverlay_MaterialComponents_Dialog
                ) {
                    viewModel.deleteUser()
                }
                return true
            }
        }
        return false
    }

    private fun validateUsername(): Boolean {
        val input = addUserUsername.editText?.text.toString().trim()
        return if (input.length < 3) {
            addUserUsername.error = resources.getString(R.string.username_invalid_error_text)
            false
        } else {
            addUserUsername.error = null
            true
        }
    }

    private fun validateName(): Boolean {
        val input = addUserName.editText?.text.toString().trim()
        return if (input.length < 3) {
            addUserName.error = resources.getString(R.string.username_invalid_error_text)
            false
        } else {
            addUserName.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val input = addUserPass.editText?.text.toString()
        Log.d("pass", input)
        return if (input.length < 6) {
            addUserPass.error = resources.getString(R.string.password_invalid_error_text)
            false
        } else {
            addUserPass.error = null
            true
        }
    }

    private fun validateConfirmPass(): Boolean {
        val input = addUserPassConfirm.editText?.text.toString()
        return if (addUserPass.editText?.text.toString() != input) {
            addUserPassConfirm.error = resources.getString(R.string.password_confirm_error_text)
            false
        } else {
            addUserPassConfirm.error = null
            true
        }
    }
}
