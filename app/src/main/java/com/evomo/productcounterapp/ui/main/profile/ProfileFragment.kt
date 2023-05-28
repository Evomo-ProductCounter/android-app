package com.evomo.productcounterapp.ui.main.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.FragmentProfileBinding
import com.evomo.productcounterapp.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

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
                val builder = AlertDialog.Builder(activity, R.style.LogoutDialog)
                with(builder) {
                    setTitle(R.string.modal_logout_title)
                    setMessage(R.string.modal_logout_desc)
                    setNegativeButton(R.string.btn_cancel) { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.cancel()
                    }
                    setPositiveButton(R.string.btn_logout) { dialogInterface: DialogInterface, i: Int ->
                        signOut()
                    }
                    setIcon(R.drawable.ic_baseline_warning_24_yellow);
                }

                val alertDialog = builder.create()
                alertDialog.show()

                val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                with(positiveButton) {
                    isAllCaps = false
                    setTextColor(resources.getColor(R.color.red))
                }

                val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                with(negativeButton) {
                    isAllCaps = false
                    setTextColor(resources.getColor(R.color.black))
                }

//                val title = alertDialog.findViewById<TextView>(android.R.id.title)
//                title.typeface = activity?.let { it1 -> ResourcesCompat.getFont(it1, R.font.poppins_semibold) }
//                AlertDialog.Builder(activity)
//                    .setTitle(R.string.modal_logout_title)
//                    .setMessage(R.string.modal_logout_desc)
//                    .setNegativeButton(R.string.btn_cancel, null)
//                    .setPositiveButton(R.string.btn_logout) { dialogInterface: DialogInterface, i: Int ->
//                        signOut()
//                    }.create().show()
//                signOut()
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