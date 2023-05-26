package com.evomo.productcounterapp.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.FragmentHomeBinding
import com.evomo.productcounterapp.databinding.FragmentProfileBinding
import com.evomo.productcounterapp.ui.camera.CameraActivity
import com.evomo.productcounterapp.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            binding.userName.text = firebaseUser.displayName.toString()
            Glide.with(this)
                .load(firebaseUser.photoUrl.toString())
                .into(binding.userPhoto)
            binding.userEmail.text = firebaseUser.email.toString()
//            binding.userMobile.text = firebaseUser.phoneNumber.toString()

            binding.logoutGroup.setOnClickListener {
                signOut()
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        activity?.let {
            GoogleSignIn.getClient(it, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut()
        } // show account chooser on login
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    companion object {
    }
}