package com.softhouse.workingout

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make no top navigation bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Set full screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        // Setup Bottom navigation bar
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        // Setup navigation controller
        val navController = findNavController(R.id.nav_host_fragment)

        // Setup link for appbar configuration
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_news, R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        // setup navigation controller with the appbar
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}