package com.adush.pexelsapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.ActivityMainBinding
import com.adush.pexelsapp.ui.home.HomeFragment
import com.adush.pexelsapp.ui.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), HomeFragment.OnDataIsProvideListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupNavController()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(alertReceiver, IntentFilter(Constants.ALERT_ACTION))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(alertReceiver)
    }

    private fun setupNavController() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
                navViewSetVisibility(destination)
        }
    }

    private fun navViewSetVisibility(destination: NavDestination){
        if (destination.id in setOf(
                R.id.navigation_details_screen
            )
        ) {
            binding.navView.visibility = View.GONE
        } else {
            binding.navView.visibility = View.VISIBLE
        }
    }

    private val alertReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra(Constants.ALERT_MESSAGE_EXTRA)?.let { showAlert(it) }
        }
    }
    private fun showAlert(msg: String) {
        val snackBar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
        snackBar.show()
    }

    override fun onDataIsProvideListener(isReady: Boolean) {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener( object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }
}