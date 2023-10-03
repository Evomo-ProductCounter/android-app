package com.evomo.productcounterapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.evomo.productcounterapp.databinding.FragmentModalTnCBinding
import com.evomo.productcounterapp.ui.ViewModelFactory
import com.evomo.productcounterapp.ui.camera.MLCameraActivity
import com.evomo.productcounterapp.ui.main.history.HistoryViewModel
import com.evomo.productcounterapp.utils.SettingViewModel
import com.evomo.productcounterapp.utils.SettingViewModelFactory

class ModalTnC : DialogFragment() {
    private lateinit var binding: FragmentModalTnCBinding
    private var shouldDismissDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModalTnCBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = obtainViewModel(activity as AppCompatActivity)
        binding.agreeButton.setOnClickListener {
            // service agree
            val userId = arguments?.getString("UserId")
            if (userId != null) {
                viewModel.agreeTnC(userId)
            }
            val activity = requireActivity() as LoginActivity
            activity.setIsAgree(true)
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

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[LoginViewModel::class.java]
    }
}