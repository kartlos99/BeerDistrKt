package com.example.beerdistrkt

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.MainActViewModel.ActUiEvent
import com.example.beerdistrkt.MainActViewModel.ActUiEvent.GoToLoginPage
import com.example.beerdistrkt.databinding.ActivityMainBinding
import com.example.beerdistrkt.databinding.ChangePassDialogBinding
import com.example.beerdistrkt.databinding.NavHeaderBinding
import com.example.beerdistrkt.fragPages.customer.presentation.CustomersFragment
import com.example.beerdistrkt.fragPages.homePage.presentation.HomeFragment
import com.example.beerdistrkt.fragPages.login.domain.model.Permission
import com.example.beerdistrkt.fragPages.login.domain.model.UserType
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.service.NotificationService
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CustomersFragment.CallPermissionInterface,
    NotificationService.NotificationInterface {

    private val vBinding by viewBinding(ActivityMainBinding::bind)

    val viewModel by viewModels<MainActViewModel>()

    private lateinit var windowInsetsController: WindowInsetsControllerCompat

    private val noToolbarScreens = setOf(
        R.id.loginFragment,
        R.id.loaderScreenFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApeniApiService.initialize(viewModel.session)
        SharedPreferenceDataSource.initialize(this)
        setContentView(R.layout.activity_main)

        vBinding.toolBar.title = getString(R.string.home_def_title)
        setSupportActionBar(vBinding.toolBar)

        setObservers()

        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        initNavController()
    }

    private fun setObservers() {
        viewModel.headerUpdateLiveData.observe(this) {
            updateNavigationView()
        }
        viewModel.eventsFlow.collectLatest(this, action = ::handleUiEvents)
    }

    private fun handleUiEvents(event: ActUiEvent) = when (event) {
        GoToLoginPage -> this.findNavController(R.id.mainNavHostFragment)
            .navigate(R.id.action_homeFragment_to_loginFragment)
    }

    private fun initNavController() {
        val navController = this.findNavController(R.id.mainNavHostFragment)

        val config = AppBarConfiguration.Builder(setOf(R.id.homeFragment))
            .setOpenableLayout(vBinding.drawerLayout)
            .build()

        NavigationUI.setupWithNavController(vBinding.toolBar, navController, config)
        setupActionBarWithNavController(navController, config)
        vBinding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                vBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                vBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(vBinding.drawerLayout.windowToken, 0)

            vBinding.toolBar.isVisible = !noToolbarScreens.contains(destination.id)
        }

        NotificationService.myNotificationInterface = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isServiceRunning()) {
            startForegroundService(Intent(this, NotificationService::class.java))
        }

        vBinding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mChangePass -> changePass()
                R.id.mLogOut -> viewModel.performUserLogout()
            }
            NavigationUI.onNavDestinationSelected(item, navController)
            vBinding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    fun setFullScreen() {
        vBinding.toolBar.isVisible = false
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    fun exitFullScreen() {
        vBinding.toolBar.isVisible = true
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }

    private fun updateNavigationView() {
        NavHeaderBinding.bind(vBinding.navView.getHeaderView(0)).apply {
            navHeaderName.text = viewModel.session.displayName
            navHeaderUsername.text = getString(
                R.string.with_brackets, viewModel.session.userName, viewModel.session.userType.name
            )
        }

        with(vBinding.navView.menu) {
            getItem(0).isEnabled = viewModel.session.hasPermission(Permission.AddEditClient)
            getItem(1).isEnabled = viewModel.session.hasPermission(Permission.AddEditUser)
            getItem(2).isEnabled = viewModel.session.userType == UserType.ADMIN
            getItem(3).isEnabled = viewModel.session.region?.hasOwnStorage == true
            findItem(R.id.settingsFragment).isEnabled = viewModel.session.userType == UserType.ADMIN
            findItem(R.id.mInfo).title = BuildConfig.VERSION_NAME
        }
    }

    private fun changePass() {
        val chPassView = ChangePassDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
        builder
            .setView(chPassView.root)
            .setTitle(R.string.change_pass)
            .setPositiveButton(R.string.text_record, null)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog?.dismiss()
            }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val oldPass = chPassView.changePassOld.text.toString()
            val newPass = chPassView.changePassNew.text.toString()
            val confPass = chPassView.changePassConfirm.text.toString()

            if (newPass.length >= 6) {
                if (newPass == confPass) {
                    viewModel.changePassword(oldPass, newPass) {
                        if (it == null) {
                            showToast(R.string.change_pass_done)
                            dialog.dismiss()
                        } else
                            showToast(it)
                    }
                } else {
                    showToast(R.string.password_confirm_error_text)
                }
            } else {
                showToast(R.string.password_invalid_error_text)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val navController = this.findNavController(R.id.mainNavHostFragment)
//        if (!navController.popBackStack())
        if (
            navController.currentDestination?.id == R.id.loginFragment ||
            navController.currentDestination?.id == R.id.homeFragment
        )
            finish()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.mainNavHostFragment)
        return NavigationUI.navigateUp(navController, vBinding.drawerLayout)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CustomersFragment.CALL_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                findNavController(R.id.mainNavHostFragment).currentDestination?.id = R.id.objListFragment
                val hostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment)
                val activeFragment = hostFragment?.childFragmentManager?.fragments?.get(0)
                if (activeFragment is CustomersFragment)
                    activeFragment.dialTo()
            }

        }
    }

    override fun askForCallPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE),
            CustomersFragment.CALL_PERMISSION_REQUEST
        )
    }

    override fun getComments() {
        val hostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment)
        val activeFragment = hostFragment?.childFragmentManager?.fragments?.get(0)
        if (activeFragment is HomeFragment)
            activeFragment.getComments()
    }

    private fun isServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (NotificationService::class.java.name.equals(service.service.className))
                return true
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        isActive = true
        updateNavigationView()
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    companion object {
        var isActive = false
    }
}
