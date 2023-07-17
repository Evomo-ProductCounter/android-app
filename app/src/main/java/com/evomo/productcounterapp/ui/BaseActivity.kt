package com.evomo.productcounterapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.ui.login.LoginActivity
import com.evomo.productcounterapp.ui.main.MainActivity
import com.evomo.productcounterapp.utils.SettingPreferences
import com.evomo.productcounterapp.utils.SettingViewModel
import com.evomo.productcounterapp.utils.SettingViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = SettingPreferences.getInstance(this.dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )

        settingViewModel.getToken().observe(this) { token ->
            if (token == "Not Set") {
                settingViewModel.clearUserPreferences()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        settingViewModel.getExpiredAt().observe(this) { expiredAt ->
            if (expiredAt != "Not Set") {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val expiredDate: Date = format.parse(expiredAt)
                val currentDate = Date()

                if (currentDate.after(expiredDate)) {
                    settingViewModel.clearUserPreferences()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }
    }

}