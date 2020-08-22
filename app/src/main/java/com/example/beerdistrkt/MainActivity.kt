package com.example.beerdistrkt

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.beerdistrkt.databinding.ActivityMainBinding
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.fragPages.homePage.HomeFragment
import com.example.beerdistrkt.fragPages.objList.ObjListFragment
import com.example.beerdistrkt.fragPages.objList.ObjListFragment.Companion.CALL_PERMISSION_REQUEST
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.service.NotificationService
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.UserType
import com.example.beerdistrkt.utils.visibleIf
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.change_pass_dialog.view.*

class MainActivity : AppCompatActivity(), ObjListFragment.CallPermissionInterface,
    NotificationService.NotificationInterface {

    private lateinit var vBinding: ActivityMainBinding

    val viewModel by lazy {
        getViewModel { MainActViewModel() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ApeniDataBase.initialize(this)
        ApeniApiService.initialize(this)
        SharedPreferenceDataSource.initialize(this)

        val toolBar = vBinding.toolBar
        toolBar.title = getString(R.string.location_ge)
        setSupportActionBar(toolBar)

        val navController = this.findNavController(R.id.mainNavHostFragment)
        navController.setGraph(R.navigation.frag_navigation)
        NavigationUI.setupActionBarWithNavController(this, navController, vBinding.drawerLayout)
        NavigationUI.setupWithNavController(vBinding.navView, navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == controller.graph.startDestination) {
                vBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                vBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(vBinding.drawerLayout.windowToken, 0)

            vBinding.toolBar.visibleIf(destination.id != R.id.loginFragment)
        }

        NotificationService.myNotificationInterface = this
        startService(Intent(this, NotificationService::class.java))

        vBinding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->

            if (item.itemId == R.id.mChangePass) {
                changePass()
            }
            NavigationUI.onNavDestinationSelected(item, navController);
            vBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return@OnNavigationItemSelectedListener true
        })

        viewModel.headerUpdateLiveData.observe(this, Observer {
            updateNavigationView()
        })

//        intent.extras?.keySet()?.forEach {
//            Log.d("extr", "$it - ${intent?.extras?.get(it!!)}")
//        }
    }

    fun updateNavigationView() {
        val userNameTv = vBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.navHeaderUsername)
        val nameTv = vBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.navHeaderName)
        userNameTv.text = "${Session.get().userName}  ( ${Session.get().userType.name} )"
        nameTv.text = Session.get().displayName

        vBinding.navView.menu.getItem(1).isEnabled = Session.get().userType == UserType.ADMIN
        vBinding.navView.menu.getItem(2).isEnabled = false
        vBinding.navView.menu.getItem(3).isEnabled = Session.get().userType == UserType.ADMIN
        vBinding.navView.menu.getItem(7).title = BuildConfig.VERSION_NAME
    }

    fun logOut() {
        val navController = this.findNavController(R.id.mainNavHostFragment)
        navController.navigate(R.id.loginFragment)
    }

    private fun changePass() {
        val chPassView: View =
            layoutInflater.inflate(R.layout.change_pass_dialog, null)

        val builder = AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
        builder
            .setView(chPassView)
            .setTitle(R.string.change_pass)
            .setPositiveButton(R.string.chawera, null)
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

    override fun onBackPressed() {
        val navController = this.findNavController(R.id.mainNavHostFragment)
//        if (!navController.popBackStack())
        if (navController.currentDestination?.id == R.id.loginFragment)
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
        if (requestCode == CALL_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                findNavController(R.id.mainNavHostFragment).currentDestination?.id = R.id.objListFragment
                val hostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment)
                val activeFragment = hostFragment?.childFragmentManager?.fragments?.get(0)
                if (activeFragment is ObjListFragment)
                    activeFragment.dialTo()
            }

        }
    }

    override fun askForCallPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE),
            CALL_PERMISSION_REQUEST
        )
    }

    override fun getComments() {
        val hostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment)
        val activeFragment = hostFragment?.childFragmentManager?.fragments?.get(0)
        if (activeFragment is HomeFragment)
            activeFragment.getComments()
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
