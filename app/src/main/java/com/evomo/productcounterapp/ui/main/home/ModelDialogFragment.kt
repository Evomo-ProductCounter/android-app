package com.evomo.productcounterapp.ui.main.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.FragmentHomeBinding
import com.evomo.productcounterapp.databinding.FragmentModelDialogBinding
import com.evomo.productcounterapp.ui.camera.CameraActivity
import com.evomo.productcounterapp.ui.camera.MLCameraActivity

class ModelDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentModelDialogBinding
    private var shouldDismissDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModelDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openCVButton.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)
            startActivity(intent)
            shouldDismissDialog = true
        }

        binding.modelOpenCVButton.setOnClickListener {
            val intent = Intent(activity, MLCameraActivity::class.java)
            startActivity(intent)
            shouldDismissDialog = true
        }
    }

    override fun onResume() {
        super.onResume()

        if (dialog?.isShowing == true && shouldDismissDialog) {
            dismiss()
        }

        shouldDismissDialog = false
    }
}