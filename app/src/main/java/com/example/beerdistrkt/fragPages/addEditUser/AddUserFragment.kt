package com.example.beerdistrkt.fragPages.addEditUser

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.AddUserFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.models.UserStatus
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.setText
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.text
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.goAway
import com.example.beerdistrkt.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddUserFragment : BaseFragment<AddUserViewModel>() {

    companion object {
        fun newInstance() = AddUserFragment()
    }

    private val binding by viewBinding(AddUserFragmentBinding::bind)

    override val viewModel by paramViewModels<AddUserViewModel, AddUserViewModel.Factory> { factory ->
        factory.create(userID)
    }

    val userID by lazy {
        AddUserFragmentArgs.fromBundle(requireArguments()).userID
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

        if (Session.get().hasPermission(Permission.ManageRegion) && userID.isNotBlank())
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
            addUserPass.isVisible = isChecked
            addUserPassConfirm.isVisible = isChecked
        }
        addUserAdminBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                addUserManagerBox.isChecked = false
        }
        addUserManagerBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                addUserAdminBox.isChecked = false
        }

        addUserDoneBtn.setOnClickListener {
            viewModel.onDoneClick(
                readUser(),
                addUserChangePassBox.isChecked,
                addUserPass.text(),
                addUserPassConfirm.text()
            )
        }
        addUserRegionBtn.setOnClickListener {
            showRegionChooser()
        }
    }

    private fun readUser() = User(
        userID,
        binding.addUserUsername.text(),
        binding.addUserName.text(),
        getUserType(),
        binding.addUserPhone.text(),
        binding.addUserAddress.text(),
        Session.get().userID ?: "0",
        binding.addUserComment.text(),
        UserStatus.ACTIVE
    )

    private fun getUserType(): String = when {
        binding.addUserAdminBox.isChecked -> UserType.ADMIN.value
        binding.addUserManagerBox.isChecked -> UserType.MANAGER.value
        else -> UserType.DISTRIBUTOR.value
    }

    private fun initViewModel() {
        viewModel.addUserLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> {
                }

                is ApiResponseState.Success -> {
                    showToast(it.data)
                    findNavController().navigateUp()
                }

                else -> {}
            }
        }
        viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                fillForm(user)
            }
        }
        viewModel.deleteUserLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(R.string.is_deleted)
                    findNavController().navigateUp()
                }

                else -> {}
            }
        }
        viewModel.userRegionsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> showRegions(it.data)
                is ApiResponseState.ApiError -> {
                    (activity as MainActivity).logOut()
                }

                else -> {}
            }
        }
        viewModel.userValidatorLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is UserValidationResult.Success -> clearFormErrors()
                is UserValidationResult.Error -> showErrors(it.errors)
            }
        }
    }

    private fun showErrors(errors: List<UserValidationResult.ErrorType>) {
        showToast(R.string.enter_corect_data)
        clearFormErrors()
        errors.forEach {
            when (it) {
                UserValidationResult.ErrorType.InvalidUsername ->
                    binding.addUserUsername.error =
                        resources.getString(R.string.username_invalid_error_text)

                UserValidationResult.ErrorType.InvalidName -> binding.addUserName.error =
                    resources.getString(R.string.username_invalid_error_text)

                UserValidationResult.ErrorType.InvalidPassword -> binding.addUserPass.error =
                    resources.getString(R.string.password_invalid_error_text)

                UserValidationResult.ErrorType.PasswordNotMatch -> binding.addUserPassConfirm.error =
                    resources.getString(R.string.password_confirm_error_text)
            }
        }
    }

    private fun clearFormErrors() {
        with(binding) {
            addUserUsername.error = null
            addUserName.error = null
            addUserPass.error = null
            addUserPassConfirm.error = null
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

    private fun fillForm(user: User) = with(binding) {
        addUserUsername.setText(user.username)
        addUserName.setText(user.name)
        addUserAdminBox.isChecked = user.type == UserType.ADMIN.value
        addUserManagerBox.isChecked = user.type == UserType.MANAGER.value
        addUserPhone.setText(user.tel)
        addUserAddress.setText(user.adress)
        addUserComment.setText(user.comment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_user_manu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mDeleteUser -> {
                showDeleteDialog()
                return true
            }
        }
        return false
    }

    private fun showDeleteDialog() = requireContext().showAskingDialog(
        R.string.delete,
        R.string.confirm_delete_text,
        R.string.yes,
        R.string.no,
        R.style.ThemeOverlay_MaterialComponents_Dialog
    ) {
        viewModel.deleteUser()
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
