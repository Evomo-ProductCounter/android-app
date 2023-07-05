package com.evomo.productcounterapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.ActivityMainBinding
import com.evomo.productcounterapp.ui.camera.CameraActivity
import com.evomo.productcounterapp.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//import com.evomo.productcounterapp.utils.createTempFile
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide title bar
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (OpenCVLoader.initDebug()) Log.d("LOADED", "success")
        else Log.d("LOADED", "error")

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}