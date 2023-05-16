package com.evomo.productcounterapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.provider.FontsContractCompat.Columns.RESULT_CODE
import com.evomo.productcounterapp.databinding.ActivityMainBinding
//import com.evomo.productcounterapp.utils.createTempFile
import org.opencv.android.OpenCVLoader
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        binding.lastCount.text = CameraActivity.lastCount
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (OpenCVLoader.initDebug()) Log.d("LOADED", "success")
        else Log.d("LOADED", "error")

        binding.cameraButton.setOnClickListener{
            val popupMenu = PopupMenu(this@MainActivity, binding.cameraButton)

            popupMenu.menuInflater.inflate(R.menu.option_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {
                    R.id.small_type -> {
                        CameraActivity.cameraWidth = 200
                        CameraActivity.cameraHeight = 80
                        CameraActivity.centerX = 100
                        CameraActivity.centerY = 35
                        val intent = Intent(this, CameraActivity::class.java)
                        resultLauncher.launch(intent)
                        true
                    }
                    R.id.medium_type -> {
                        CameraActivity.cameraWidth = 400
                        CameraActivity.cameraHeight = 150
                        CameraActivity.centerX = 200
                        CameraActivity.centerY = 75
                        val intent = Intent(this, CameraActivity::class.java)
                        resultLauncher.launch(intent)
                        true
                    }
                    R.id.large_type -> {
                        CameraActivity.cameraWidth = 800
                        CameraActivity.cameraHeight = 300
                        CameraActivity.centerX = 400
                        CameraActivity.centerY = 150
                        val intent = Intent(this, CameraActivity::class.java)
                        resultLauncher.launch(intent)
                        true
                    }
                    else -> {
                        super.onOptionsItemSelected(menuItem)
                    }
                }
            }
            popupMenu.show()
        }
    }
}