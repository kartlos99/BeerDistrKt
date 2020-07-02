package com.example.beerdistrkt

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.beerdistrkt.adapters.MyPagesAdapter
import com.example.beerdistrkt.databinding.ActivityMainBinding
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.fragPages.amonaweri.AmonaweriSubPageFrag
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.visibleIf
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var vBinding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ApeniDataBase.initialize(this)
        ApeniApiService.initialize(this)
        drawerLayout = vBinding.drawerLayout

        val toolBar = vBinding.toolBar
        toolBar.title = getString(R.string.location_ge)
        setSupportActionBar(toolBar)

        val navController = this.findNavController(R.id.mainNavHostFragment)
        navController.setGraph(R.navigation.frag_navigation)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(vBinding.navView, navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == controller.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(vBinding.drawerLayout.windowToken, 0)

            vBinding.toolBar.visibleIf(destination.id != R.id.loginFragment)
        }

//        vBinding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
//            item.setTitle(R.string.yes)
//            return@OnNavigationItemSelectedListener true
//        })
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
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}
