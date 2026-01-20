package hr.algebra.moviedb

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import hr.algebra.moviedb.databinding.ActivityHostBinding
import hr.algebra.moviedb.framework.PermissionHelper

private const val KEY_DRAWER_OPEN = "drawer_open"

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding
    private var isDrawerOpen = false
    
    // Register the permission launcher for notification permission
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle the permission result
        if (isGranted) {
            // Permission granted - notifications will work
        } else {
            // Permission denied - notifications won't be shown
            // App continues to work without notifications
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleTransition()
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initHamburgerMenu()
        initNavigation()
        
        // Request notification permission on Android 13+
        requestNotificationPermissionIfNeeded()
        
        // Restore drawer state
        savedInstanceState?.let {
            isDrawerOpen = it.getBoolean(KEY_DRAWER_OPEN, false)
            if (isDrawerOpen) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
    
    private fun requestNotificationPermissionIfNeeded() {
        if (!PermissionHelper.hasNotificationPermission(this)) {
            // Check if we should show rationale
            if (PermissionHelper.shouldShowNotificationRationale(this)) {
                // Show explanation dialog before requesting
                AlertDialog.Builder(this)
                    .setTitle(R.string.notification_permission_title)
                    .setMessage(R.string.notification_permission_message)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        PermissionHelper.requestNotificationPermission(notificationPermissionLauncher)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            } else {
                // Request permission directly
                PermissionHelper.requestNotificationPermission(notificationPermissionLauncher)
            }
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save drawer state
        outState.putBoolean(KEY_DRAWER_OPEN, binding.drawerLayout.isDrawerOpen(GravityCompat.START))
    }

    private fun handleTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.host_menu, menu)
        return true
    }

    private fun initHamburgerMenu() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                toggleDrawer()
            }
            R.id.miExit -> {
                exitApp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitApp() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.exit)
            setMessage(getString(R.string.do_you_really_want_to_exit_an_app))
            setIcon(R.drawable.exit)
            setCancelable(true)
            setNegativeButton(getString(R.string.cancel), null)
            setPositiveButton("OK") { _, _ -> finish() }
            show()
        }
    }

    private fun toggleDrawer() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun initNavigation() {
        val navController = findNavController(this, R.id.navController)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }
}