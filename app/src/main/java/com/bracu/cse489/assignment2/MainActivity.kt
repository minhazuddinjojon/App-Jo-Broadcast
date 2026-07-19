package com.bracu.cse489.assignment2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bracu.cse489.assignment2.databinding.ActivityMainBinding

/**
 * CSE 489 - Assignment 2
 * Single-Activity host for the Navigation Drawer + Jetpack Navigation Component.
 * The drawer's four destinations (Broadcast Receiver, Image Scale, Video, Audio)
 * are all Fragments swapped inside [R.id.nav_host_fragment_content_main].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Top-level destinations shown directly in the drawer menu.
        // Any other screen (battery result, text input, custom receiver) shows a Back arrow instead.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.broadcastSelectionFragment,
                R.id.imageScaleFragment,
                R.id.videoFragment,
                R.id.audioFragment
            ),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
