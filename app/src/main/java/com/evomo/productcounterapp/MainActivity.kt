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
//        if (result.data != null) {
//            val selectedValue =
//                result.data?.getStringExtra(CameraActivity.lastCount)
//            binding.lastCount.text = "Hasil : $selectedValue"
        binding.lastCount.text = CameraActivity.lastCount
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (OpenCVLoader.initDebug()) Log.d("LOADED", "success")
        else Log.d("LOADED", "error")

        binding.cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            resultLauncher.launch(intent)
//            startActivity(intent)
        }


    }
}