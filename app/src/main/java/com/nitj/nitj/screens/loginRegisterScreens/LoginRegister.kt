package com.nitj.nitj.screens.loginRegisterScreens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nitj.nitj.R

class LoginRegister : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_NITJ)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        findViews()
    }
    private fun findViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.login_register_nav_host_fragment) as NavHostFragment?
                ?: return
        navController = navHostFragment.navController
    }
}