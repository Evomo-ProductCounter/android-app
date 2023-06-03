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
import com.evomo.productcounterapp.utils.DateHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

//    private val resultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        binding.lastCount.text = CameraActivity.lastCount
//    }

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

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        var name = ""

        if (firebaseUser != null) {
            name = firebaseUser.displayName.toString()
        }

        binding.titleWelcome.text = resources.getString(R.string.title_welcome, name.split(" ").firstOrNull())

        binding.date.text = DateHelper.getCurrentDateNoTime()

        binding.cameraButton.setOnClickListener{
            val intent = Intent(activity, CameraActivity::class.java)
            startActivity(intent)
//            resultLauncher.launch(intent)
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