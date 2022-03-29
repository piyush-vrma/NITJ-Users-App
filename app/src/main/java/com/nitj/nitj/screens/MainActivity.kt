package com.nitj.nitj.screens

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.nitj.nitj.R
import com.nitj.nitj.firebaseNotificationJava.Constant.TOPIC
import com.nitj.nitj.screens.loginRegisterScreens.LoginRegister


class MainActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    val REQUEST_CODE = 1
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_CODE
        )
        findViews()

        navigationView.menu.findItem(R.id.logOut).setOnMenuItemClickListener {
            logOut()
            true
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }

    private fun logOut() {
        closeDrawer()
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("LOGOUT")
        dialog.setIcon(R.drawable.ic_alert)
        dialog.setMessage("Are you sure you want to Logout?")
        dialog.setPositiveButton("Yes") { text, listener ->
            firebaseAuth.signOut()
            openLogin()
        }
        dialog.setNegativeButton("No") { text, listener ->
            val currentFragment =
                NavHostFragment.findNavController(navHostFragment).currentDestination?.id
            if (currentFragment == R.id.home_dest) {
                navigationView.menu.getItem(0).isChecked = true
            } else if (currentFragment == R.id.gallery_dest) {
                navigationView.menu.getItem(4).isChecked = true
            } else if (currentFragment == R.id.profile_dest) {
                navigationView.menu.getItem(5).isChecked = true
            } else if (currentFragment == R.id.about_dest) {
                navigationView.menu.getItem(6).isChecked = true
            }
        }
        dialog.create()
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser == null) {
            openLogin()
        }
    }


    private fun openLogin() {
        val intent = Intent(this, LoginRegister::class.java)
        startActivity(intent)
        finish()
    }

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)
        firebaseAuth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigation_view)
        // navigationView.itemIconTintList = null;

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment?
                ?: return
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_dest,
                R.id.notice_dest,
                R.id.department_dest,
                R.id.gallery_dest,
                R.id.ebook_dest,
                R.id.profile_dest,
                R.id.about_dest,
                R.id.logOut
            ),
            drawerLayout
        )

        setupActionBar(navController, appBarConfiguration)

        setupNavigationMenu(navController)
    }

    private fun setupActionBar(
        navController: NavController,
        appBarConfig: AppBarConfiguration
    ) {
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun setupNavigationMenu(navController: NavController) {
        navigationView.setupWithNavController(navController)
    }

    // Have the NavigationUI look for an action or destination matching the menu
    // item id and navigate there if found.
    // Otherwise, bubble up to the parent.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.my_nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }


    //Have NavigationUI handle up behavior in the ActionBar
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.my_nav_host_fragment).navigateUp(appBarConfiguration)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed()
        }
    }

    fun closeDrawer() {
        drawerLayout.closeDrawers()
    }

    fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawerLayout.setDrawerLockMode(lockMode)
    }
}