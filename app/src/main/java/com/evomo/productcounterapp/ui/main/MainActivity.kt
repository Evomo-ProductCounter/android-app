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

//    private val resultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        binding.lastCount.text = CameraActivity.lastCount
//    }

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

//        binding.cameraButton.setOnClickListener{
//            val popupMenu = PopupMenu(this@MainActivity, binding.cameraButton)
//
//            popupMenu.menuInflater.inflate(R.menu.option_menu, popupMenu.menu)
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//
//                when (menuItem.itemId) {
//                    R.id.small_type -> {
//                        CameraActivity.cameraWidth = 200
//                        CameraActivity.cameraHeight = 80
//                        CameraActivity.centerX = 100
//                        CameraActivity.centerY = 35
//                        val intent = Intent(this, CameraActivity::class.java)
//                        resultLauncher.launch(intent)
//                        true
//                    }
//                    R.id.medium_type -> {
//                        CameraActivity.cameraWidth = 400
//                        CameraActivity.cameraHeight = 150
//                        CameraActivity.centerX = 200
//                        CameraActivity.centerY = 75
//                        val intent = Intent(this, CameraActivity::class.java)
//                        resultLauncher.launch(intent)
//                        true
//                    }
//                    R.id.large_type -> {
//                        CameraActivity.cameraWidth = 800
//                        CameraActivity.cameraHeight = 300
//                        CameraActivity.centerX = 400
//                        CameraActivity.centerY = 150
//                        val intent = Intent(this, CameraActivity::class.java)
//                        resultLauncher.launch(intent)
//                        true
//                    }
//                    else -> {
//                        super.onOptionsItemSelected(menuItem)
//                    }
//                }
//            }
//            popupMenu.show()
//        }
    }
}