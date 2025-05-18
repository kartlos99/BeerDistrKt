package com.example.beerdistrkt.fragPages.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.MainActViewModel
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.LoginFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.hide
import com.example.beerdistrkt.utils.show
import com.example.beerdistrkt.utils.visibleIf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>() {

    private val binding by viewBinding(LoginFragmentBinding::bind)

    override val viewModel: LoginViewModel by viewModels()

    private lateinit var actViewModel: MainActViewModel

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actViewModel = (activity as MainActivity).viewModel
        with(binding) {
            viewLoginLoginBtn.setOnClickListener {
                viewModel.logIn(
                    viewLoginUserField.text.toString(),
                    viewLoginPasswordField.text.toString()
                )
                viewLoginLoginBtn.isEnabled = false
                viewLoginProgress.visibleIf(true)
            }
            tvBuildInfo.text = getBuildInfo()
            if (BuildConfig.DEBUG) {
                viewLoginUserField.setText("kartlos")
                viewLoginPasswordField.setText("0000")
            }
        }

        if (viewModel.session.isUserLogged())
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        else
            checkSavedPass()

        initViewModel()
    }

    private fun getBuildInfo(): String {
        return if (BuildConfig.DEBUG) "${BuildConfig.BUILD_TYPE} ${BuildConfig.FLAVOR} ${BuildConfig.VERSION_NAME}"
        else BuildConfig.VERSION_NAME
    }

    private fun checkSavedPass() {
        val userName = SharedPreferenceDataSource.getInstance().getUserName()
        val password = SharedPreferenceDataSource.getInstance().getPass()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            with(binding) {
                viewLoginUserField.setText(userName)
                viewLoginPasswordField.setText(password)
                viewModel.logIn(userName, password)
                viewLoginLoginBtn.isEnabled = false
                viewLoginProgress.visibleIf(true)
            }
        }
    }

    private fun initViewModel() {
        viewModel.loginResponseLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                when (it) {
                    is ApiResponseState.Success -> {
                        if (viewLoginSaveChk.isChecked) {
                            SharedPreferenceDataSource(requireContext()).saveUserName(it.data.username)
                            SharedPreferenceDataSource(requireContext()).savePassword(
                                viewLoginPasswordField.text.toString()
                            )
                        }
                        viewLoginLoginBtn.isEnabled = false
                        viewLoginProgress.show()
                        afterSuccessResponse(it.data)
                        viewModel.loginResponseLiveData.value = ApiResponseState.Sleep
                    }

                    is ApiResponseState.ApiError -> {
                        viewLoginLoginBtn.isEnabled = true
                        viewLoginProgress.hide()
                        showToast(it.errorText)
                    }

                    is ApiResponseState.Loading -> {
                        if (!it.showLoading) {
                            viewLoginLoginBtn.isEnabled = true
                            viewLoginProgress.hide()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun afterSuccessResponse(data: LoginResponse) {
        when {
            data.regions.isNotEmpty() -> proceedLogin(data)
            else -> {
                showToast(R.string.no_regions_accosiated)
                binding.viewLoginLoginBtn.isEnabled = true
                binding.viewLoginProgress.hide()
            }
        }
    }

    private fun proceedLogin(data: LoginResponse) {
        viewModel.setUserData(data)
        loginToFirebase(data.username)
    }

    private fun loginToFirebase(username: String) {
        val userMail = "$username@apeni.ge"
        mAuth.signInWithEmailAndPassword(userMail, BuildConfig.FIREBASE_PASS)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("auth", "SignIN:success")
                    onLoginSuccess()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener {
                        Log.d("token-msg", it.result, it.exception)
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        registerInFirebase(userMail)
                        Log.d("auth", "exp_MEssage: " + task.exception?.message)
                    } else {
                        showToast(R.string.auth_fail_firebase)
                        Log.d("auth", "exp_MEssage: " + task.exception?.message)
                        viewModel.session.clearSession()
                    }
                }
            }
    }

    private fun registerInFirebase(username: String) {
        mAuth.createUserWithEmailAndPassword(username, BuildConfig.FIREBASE_PASS)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("auth", "createUserWithEmail:success")
//                    val user = mAuth.currentUser
                    onLoginSuccess()
                } else {
                    Log.d("auth", task.exception?.message ?: "")
                    showToast(R.string.registration_fail_firebase)
                    viewModel.session.clearSession()
                }
            }
    }

    private fun onLoginSuccess() {
        binding.viewLoginLoginBtn.isEnabled = true
        binding.viewLoginProgress.hide()
        actViewModel.updateNavHeader()
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

}
