package com.evomo.productcounterapp.ui.main.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.FragmentHomeBinding
import com.evomo.productcounterapp.ui.camera.CameraActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        binding.lastCount.text = CameraActivity.lastCount
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraButton.setOnClickListener{
            val popupMenu = PopupMenu(requireContext(), binding.cameraButton)

            popupMenu.menuInflater.inflate(R.menu.option_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {
                    R.id.small_type -> {
                        CameraActivity.cameraWidth = 200
                        CameraActivity.cameraHeight = 80
                        CameraActivity.centerX = 100
                        CameraActivity.centerY = 35
                        val intent = Intent(activity, CameraActivity::class.java)
                        resultLauncher.launch(intent)
                        true
                    }
                    R.id.medium_type -> {
                        CameraActivity.cameraWidth = 400
                        CameraActivity.cameraHeight = 150
                        CameraActivity.centerX = 200
                        CameraActivity.centerY = 75
                        val intent = Intent(activity, CameraActivity::class.java)
                        resultLauncher.launch(intent)
                        true
                    }
                    R.id.large_type -> {
                        CameraActivity.cameraWidth = 800
                        CameraActivity.cameraHeight = 300
                        CameraActivity.centerX = 400
                        CameraActivity.centerY = 150
                        val intent = Intent(activity, CameraActivity::class.java)
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

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
}