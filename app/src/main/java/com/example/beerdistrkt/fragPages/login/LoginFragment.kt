package com.example.beerdistrkt.fragPages.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.*

import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.PrivateKey
import com.example.beerdistrkt.utils.Session
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : BaseFragment<LoginViewModel>() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override val viewModel by lazy {
        getViewModel { LoginViewModel() }
    }

    lateinit var actViewModel: MainActViewModel

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
        viewLoginLoginBtn.setOnClickListener {
            viewModel.logIn(
                viewLoginUserField.text.toString(),
                viewLoginPasswordField.text.toString()
            )
            viewLoginLoginBtn.isEnabled = false
        }

        if (Session.get().isUserLogged()) {
            Session.get().clearSession()
            SharedPreferenceDataSource.getInstance().saveUserName("")
            SharedPreferenceDataSource.getInstance().savePassword("")
        } else
            checkSavedPass()

        initViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        actViewModel = (activity as MainActivity).viewModel
    }

    private fun checkSavedPass() {
        val userName = SharedPreferenceDataSource.getInstance().getUserName()
        val password = SharedPreferenceDataSource.getInstance().getPass()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            viewLoginUserField.setText(userName)
            viewLoginPasswordField.setText(password)
            viewModel.logIn(userName, password)
            viewLoginLoginBtn.isEnabled = false
        }
    }

    private fun initViewModel() {
        viewModel.loginResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    if (viewLoginSaveChk.isChecked) {
                        SharedPreferenceDataSource(requireContext()).saveUserName(it.data.username)
                        SharedPreferenceDataSource(requireContext()).savePassword(
                            viewLoginPasswordField.text.toString()
                        )
                    }
                    viewLoginLoginBtn.isEnabled = false
                    loginToFirebase(it.data.username)
                    viewModel.loginResponseLiveData.value = ApiResponseState.Sleep
                }
                is ApiResponseState.ApiError -> {
                    viewLoginLoginBtn.isEnabled = true
                    showToast(it.errorText)
                }
                is ApiResponseState.Loading -> {
                    if (!it.showLoading)
                        viewLoginLoginBtn.isEnabled = true
                }
            }
        })
    }

    private fun loginToFirebase(username: String) {
        val userMail = "$username@apeni.ge"
        mAuth.signInWithEmailAndPassword(userMail, PrivateKey.FIREBASE_PASS)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("auth", "SignIN:success")
                    onLoginSuccess()
                    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                        val newToken = it.token
                        Log.d("token", newToken)
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        registerInFirebase(userMail)
                        Log.d("auth", "exp_MEssage: " + task.exception?.message)
                    } else {
                        showToast(R.string.auth_fail_firebase)
                        Log.d("auth", "exp_MEssage: " + task.exception?.message)
                        Session.get().clearSession()
                    }
                }
            }
    }

    private fun registerInFirebase(username: String) {
        mAuth.createUserWithEmailAndPassword(username, PrivateKey.FIREBASE_PASS)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("auth", "createUserWithEmail:success")
//                    val user = mAuth.currentUser
                    onLoginSuccess()
                } else {
                    Log.d("auth", task.exception?.message ?: "")
                    showToast(R.string.registration_fail_firebase)
                    Session.get().clearSession()
                }
            }
    }

    private fun onLoginSuccess() {
        viewLoginLoginBtn?.isEnabled = true
        actViewModel.updateNavHeader()
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

}
