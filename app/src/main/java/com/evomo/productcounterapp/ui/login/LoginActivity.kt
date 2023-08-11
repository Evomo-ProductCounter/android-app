package com.evomo.productcounterapp.ui.login

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.ActivityLoginBinding
import com.evomo.productcounterapp.ui.ViewModelFactory
import com.evomo.productcounterapp.ui.main.MainActivity
import com.evomo.productcounterapp.utils.SettingPreferences
import com.evomo.productcounterapp.utils.SettingViewModel
import com.evomo.productcounterapp.utils.SettingViewModelFactory
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var username: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide title bar
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val manufacturer = Build.MANUFACTURER
//        val model = Build.MODEL
        val ramInMb = getTotalRAM()
        val numCores = getNumCores()

//        Log.d("TEST", "Manufacturer $manufacturer")
//        Log.d("TEST", "Model: $model")
//        Log.d("TEST", "Total RAM: $ramInMb MB")
//        Log.d("TEST", "Number of CPU Cores: $numCores")

        if (ramInMb < 4000 || numCores < 4) {
            val builder = AlertDialog.Builder(this, R.style.LogoutDialog)
            with(builder) {
                setTitle(R.string.modal_device_error_title)
                setMessage(R.string.modal_device_error)
                setNegativeButton(R.string.btn_exit) { dialogInterface: DialogInterface, i: Int ->
                    finishAffinity()
                }
                setIcon(R.drawable.ic_baseline_warning_24_yellow);
            }

            val alertDialog = builder.create()
            alertDialog.show()

            val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            with(negativeButton) {
                isAllCaps = false
                setTextColor(resources.getColor(R.color.red))
            }
        }

        val viewModel = obtainViewModel(this)
        val pref = SettingPreferences.getInstance((this).dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )

        binding.buttonSignin.setOnClickListener {
            username = binding.edLoginUsername.text.toString().trim()
            password = binding.edLoginPassword.text.toString().trim()

            if (TextUtils.isEmpty(username)) {
                binding.edLoginUsername.error = "Field must be filled"
            } else if (TextUtils.isEmpty(password)) {
                binding.edLoginPassword.error = "Field must be filled"
            } else if (binding.edLoginPassword.error?.length ?: 0 > 0) {
                binding.edLoginPassword.requestFocus()
            }
            else {
                viewModel.login(username, password)
                viewModel.loginUser.observe(this) { loginResponse ->
                    if (loginResponse.success) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                viewModel.isError.observe(this) { isError ->
                    if (isError) {
                        val builder = AlertDialog.Builder(this, R.style.LogoutDialog)
                        with(builder) {
                            setTitle(R.string.modal_login_error_title)
                            setMessage(R.string.modal_login_error)
                            setNegativeButton(R.string.btn_try_again) { dialogInterface: DialogInterface, i: Int ->
                                dialogInterface.cancel()
                            }
                            setIcon(R.drawable.ic_baseline_warning_24_yellow);
                        }

                        val alertDialog = builder.create()
                        alertDialog.show()

                        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                        with(negativeButton) {
                            isAllCaps = false
                            setTextColor(resources.getColor(R.color.red))
                        }
                    }
                }
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.loginUser.observe(this) { // get user login response and save to datastore
                login ->
//            val currentDate = Date()
            val currentDate = Calendar.getInstance().time
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
//            calendar.add(Calendar.HOUR_OF_DAY, 2)
            calendar.add(Calendar.MINUTE, 100)
            val expiredDate = calendar.time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val formattedDate = dateFormat.format(expiredDate)

            settingViewModel.setUserPreferences(
                login.data.accessToken,
                login.data.name,
                username,
                login.data.userid,
//                login.data.expiredAt
                formattedDate.toString()
            )
        }

        showLoading(true)
        settingViewModel.getToken().observe(this) { token ->
            if (token != "Not Set") {
                val mIntent = Intent(this, MainActivity::class.java)
                startActivity(mIntent)
            }
            showLoading(false)
        }
    }

    private fun getTotalRAM(): Long {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMemoryInBytes = memoryInfo.totalMem
        val totalMemoryInMb = totalMemoryInBytes / (1024 * 1024)

        return totalMemoryInMb
    }

    private fun getNumCores(): Int {
        return Runtime.getRuntime().availableProcessors()
    }

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[LoginViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}