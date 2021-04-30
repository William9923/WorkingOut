package com.softhouse.workingout.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.softhouse.workingout.R
import com.softhouse.workingout.shared.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Set layout
        setContentView(R.layout.activity_main)

        // Check if needed to navigate to main fragment
        navigateToTrackingFragmentIfNeeded(intent)

        // Setup Bottom navigation bar
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // Setup navigation controller
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navFragment.navController

        // Setup link for appbar configuration
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_news, R.id.navigation_tracking, R.id.navigation_calender, R.id.navigation_schedule_list
            )
        )

        // setup navigation controller with the appbar
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // To Support Navigate Back Button
    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    // Check if needed to navigate to main fragment
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val navView: BottomNavigationView = findViewById(R.id.nav_view)
            navView.visibility = VISIBLE

            // Show AppBar
            supportActionBar?.show()
        } else {
            val navView: BottomNavigationView = findViewById(R.id.nav_view)
            navView.visibility = GONE

            // Hide AppBar
            supportActionBar?.hide()
        }
    }

    // To navigate back to tracking menu
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            // Setup navigation controller
            val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navFragment.navController
            navController.navigate(R.id.action_global_trackingFragment)
        }
    }

}