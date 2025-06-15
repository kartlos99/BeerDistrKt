package com.example.beerdistrkt.fragPages.user.presentation.modify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.AddUserFragmentBinding
import com.example.beerdistrkt.fragPages.login.domain.model.UserType
import com.example.beerdistrkt.fragPages.user.domain.UserValidationResult
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.presentation.model.RegionChipItem
import com.example.beerdistrkt.models.UserStatus
import com.example.beerdistrkt.network.model.isLoading
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.setText
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.text
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.hide
import com.google.android.material.chip.Chip
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

    override val titleRes: Int
        get() = if (userID.isEmpty()) R.string.add_user else R.string.m_edit

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

    }

    private fun AddUserFragmentBinding.initView() {
        if (userID.isEmpty()) {
            addUserChangePassBox.hide()
        } else {
            addUserPass.hide()
            addUserPassConfirm.hide()
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
            saveFormState()
            viewModel.saveUserData()
        }
    }

    private fun readUser() = User(
        id = userID,
        username = binding.addUserUsername.text(),
        name = binding.addUserName.text(),
        getUserType(),
        tel = binding.addUserPhone.text(),
        address = binding.addUserAddress.text(),
        maker = viewModel.session.userID ?: "0",
        comment = binding.addUserComment.text(),
        userStatus = UserStatus.ACTIVE,
        regions = listOf()
    )

    override fun onPause() {
        super.onPause()
        saveFormState()
    }

    private fun saveFormState() {
        viewModel.saveFormState(
            readUser(),
            binding.userRegionChipsGroup.checkedChipIds,
            binding.addUserChangePassBox.isChecked,
            binding.addUserPass.text(),
            binding.addUserPassConfirm.text()
        )
    }

    private fun getUserType(): UserType = when {
        binding.addUserAdminBox.isChecked -> UserType.ADMIN
        binding.addUserManagerBox.isChecked -> UserType.MANAGER
        else -> UserType.DISTRIBUTOR
    }

    private fun initViewModel() {
        viewModel.apiState.collectLatest(viewLifecycleOwner) { apiResult ->
            binding.progressIndicator.isVisible = apiResult.isLoading()
            apiResult.onSuccess {
                it?.let {
                    showToast(R.string.data_saved)
                    findNavController().navigateUp()
                }
            }
            apiResult.onError { code, message ->
                showToast(message)
            }
        }
        viewModel.uiState.collectLatest(viewLifecycleOwner) { userData ->
            fillForm(
                userData.user,
                userData.isChangingPassword,
                userData.password,
                userData.confirmPassword,
            )
            binding.userRegionChipsGroup.isVisible = userData.canModifyRegion
            binding.addUserRegionsTitle.isVisible = userData.canModifyRegion
            if (userData.canModifyRegion) {
                drawRegions(userData.regionChips)
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

                UserValidationResult.ErrorType.NoRegionSet -> showToast(R.string.no_region_set_error)
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

    private fun fillForm(
        user: User?,
        isChangingPassword: Boolean,
        password: String,
        confirmPassword: String,
    ) = with(binding) {
        if (user == null) return
        addUserUsername.setText(user.username)
        addUserName.setText(user.name)
        addUserAdminBox.isChecked = user.type == UserType.ADMIN
        addUserManagerBox.isChecked = user.type == UserType.MANAGER
        addUserPhone.setText(user.tel)
        addUserAddress.setText(user.address)
        addUserComment.setText(user.comment)

        addUserPass.setText(password)
        addUserPassConfirm.setText(confirmPassword)
        addUserChangePassBox.isChecked = isChangingPassword
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

    private fun drawRegions(regionItems: List<RegionChipItem>) {
        binding.userRegionChipsGroup.removeAllViews()
        regionItems.forEach { item ->
            val chip = Chip(context).apply {
                id = item.id
                text = item.name
                checkedIcon = ResourcesCompat.getDrawable(resources, R.drawable.check_24, null)
                isCheckable = true
                isChecked = item.isAttached
                chipStrokeWidth = 4f
                setChipStrokeColorResource(R.color.region_chip_color)
                chipBackgroundColor =
                    AppCompatResources.getColorStateList(context, R.color.barrel_chip_color)
            }
            binding.userRegionChipsGroup.addView(chip)
        }
    }

}
