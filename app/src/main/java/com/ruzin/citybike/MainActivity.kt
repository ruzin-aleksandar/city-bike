package com.ruzin.citybike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.activity.viewModels
import com.ruzin.citybike.databinding.ActivityMainBinding
import com.ruzin.citybike.viewmodel.FragmentViewModel
import com.ruzin.citybike.viewmodel.ViewModelFactory

/**
 * Main [AppCompatActivity] containing the frame layout that contains the two fragments that show the list of bike companies, and the map screen
 */

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    //viewModel shared by the main activity and both fragments
    private val viewModel: FragmentViewModel by viewModels { ViewModelFactory(this) }

    //setting up the fragment layout and navigation graph
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onResume() {
        super.onResume()
        //get the selected cache timeout when the activity shows on the screen
        viewModel.getSelectedCacheTimeout()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // intent for starting the settings activity when the user taps Settings in the options menu
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}