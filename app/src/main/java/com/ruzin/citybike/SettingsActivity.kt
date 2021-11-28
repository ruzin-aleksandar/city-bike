package com.ruzin.citybike

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ruzin.citybike.databinding.ActivitySettingsBinding
import com.ruzin.citybike.model.Constants
/**
 * Settings [AppCompatActivity] as the settings screen of the application allowing the user to change the timeout of the bikeCompanies cache.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    var seekBar: SeekBar? = null
    var cactheTimeoutLabel : TextView? = null
    var cacheTimeout: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.action_settings)
        //accessing the SharedPreferences API
        val sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        //checking if there is a saved value for the cache timeout key, getting a default value of 15 if there isn't a saved value
        cacheTimeout = sharedPreferences.getInt(Constants.CACHE_KEY, 15)
        cactheTimeoutLabel = binding.mainContainer.cacheExpiry
        cactheTimeoutLabel!!.text = getString(R.string.cache_minutes, cacheTimeout)
        seekBar = binding.mainContainer.cacheSeekBar
        seekBar!!.min = Constants.CACHE_MIN
        seekBar!!.max = Constants.CACHE_MAX
        seekBar!!.progress = cacheTimeout
        seekBar!!.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            var preferencesEditor : SharedPreferences.Editor? = null

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                 // changing the selected value textView on seekbar progress update
                binding.mainContainer.cacheExpiry.text = getString(R.string.cache_minutes, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // setting up the sharedPreferences editor when the user starts interacting with the seekbar
                preferencesEditor = sharedPreferences.edit()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // saving the selected cache timeout value to sharedPreferences when the user stops the seekbar interaction
                preferencesEditor?.putInt(Constants.CACHE_KEY, seekBar!!.progress)
                preferencesEditor?.apply()
            }
        }
        )
    }
}