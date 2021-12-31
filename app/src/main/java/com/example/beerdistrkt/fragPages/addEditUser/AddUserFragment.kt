package com.example.beerdistrkt.fragPages.addEditUser

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.AddUserFragmentBinding
import com.example.beerdistrkt.fragPages.addEditUser.models.AddUserRequestModel
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.*

class AddUserFragment : BaseFragment<AddUserViewModel>() {

    companion object {
        fun newInstance() = AddUserFragment()
    }

    private val binding by viewBinding(AddUserFragmentBinding::bind)

    override val viewModel by lazy {
        getViewModel { AddUserViewModel(userID) }
    }

    var userType = UserType.DISTRIBUTOR

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
        binding.initView()

        if (Session.get().hasPermission(Permission.ManageRegion) && !userID.isBlank())
            viewModel.getRegionForUser()
    }

    private fun AddUserFragmentBinding.initView() {
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
        addUserAdminBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userType = UserType.ADMIN
                addUserManagerBox.isChecked = false
            } else
                userType = UserType.DISTRIBUTOR
        }
        addUserManagerBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userType = UserType.MANAGER
                addUserAdminBox.isChecked = false
            } else
                userType = UserType.DISTRIBUTOR
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
        addUserRegionBtn.setOnClickListener {
            showRegionChooser()
        }
    }

    private fun isFormValid(): Boolean {
        return if (userID.isNotEmpty() && !binding.addUserChangePassBox.isChecked)
            validateUsername() and validateName()
        else
            validateUsername() and validateName() and validatePassword() and validateConfirmPass()
    }

    private fun readUser(): User {
        return User(
            userID,
            binding.addUserUsername.editText?.text.toString(),
            binding.addUserName.editText?.text.toString(),
            userType.value,
            binding.addUserPhone.editText?.text.toString(),
            binding.addUserAddress.editText?.text.toString(),
            Session.get().userID ?: "0",
            binding.addUserComment.editText?.text.toString()
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
        viewModel.userRegionsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> showRegions(it.data)
                is ApiResponseState.ApiError -> {
                    SharedPreferenceDataSource.getInstance().saveRegion(null)
                    (activity as MainActivity).logOut()
                }
            }
        }
    }

    private fun showRegions(data: List<AttachedRegion>) {
        val regionsString = data
            .joinToString(", ", getString(R.string.regions) + " ") { it.name }
        with(binding) {
            addUserRegionsTv.text = regionsString
            addUserRegionsTv.show()
            addUserRegionBtn.show()
        }
    }

    private fun fillForm(user: User) {
        with(binding) {
            addUserUsername.editText?.setText(user.username)
            addUserName.editText?.setText(user.name)
            addUserAdminBox.isChecked = user.type == UserType.ADMIN.value
            addUserManagerBox.isChecked = user.type == UserType.MANAGER.value
            addUserPhone.editText?.setText(user.tel)
            addUserAddress.editText?.setText(user.adress)
            addUserComment.editText?.setText(user.comment)
        }
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
        val input = binding.addUserUsername.editText?.text.toString().trim()
        return if (input.length < 3) {
            binding.addUserUsername.error =
                resources.getString(R.string.username_invalid_error_text)
            false
        } else {
            binding.addUserUsername.error = null
            true
        }
    }

    private fun validateName(): Boolean {
        val input = binding.addUserName.editText?.text.toString().trim()
        return if (input.length < 3) {
            binding.addUserName.error = resources.getString(R.string.username_invalid_error_text)
            false
        } else {
            binding.addUserName.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val input = binding.addUserPass.editText?.text.toString()
        Log.d("pass", input)
        return if (input.length < 6) {
            binding.addUserPass.error = resources.getString(R.string.password_invalid_error_text)
            false
        } else {
            binding.addUserPass.error = null
            true
        }
    }

    private fun validateConfirmPass(): Boolean {
        val input = binding.addUserPassConfirm.editText?.text.toString()
        return if (binding.addUserPass.editText?.text.toString() != input) {
            binding.addUserPassConfirm.error =
                resources.getString(R.string.password_confirm_error_text)
            false
        } else {
            binding.addUserPassConfirm.error = null
            true
        }
    }

    private fun showRegionChooser() {
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.associated_regions))
            .setCancelable(true)
            .setMultiChoiceItems(
                viewModel.getAllRegionNames(),
                viewModel.getSelectedRegions()
            ) { dialogInterface: DialogInterface?, i: Int, b: Boolean ->
                if (b)
                    viewModel.selectedRegions.add(viewModel.regions[i])
                else
                    viewModel.selectedRegions.remove(viewModel.regions[i])
            }
            .setPositiveButton(R.string.common_save) { _, _ ->
                viewModel.setNewRegions()
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, i -> }

        val alertDialog = builder.create()
        alertDialog.show()

    }
}
