package com.evomo.productcounterapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.evomo.productcounterapp.databinding.ActivityLoginBinding
import com.evomo.productcounterapp.ui.ViewModelFactory
import com.evomo.productcounterapp.ui.main.MainActivity
import com.evomo.productcounterapp.utils.SettingPreferences
import com.evomo.productcounterapp.utils.SettingViewModel
import com.evomo.productcounterapp.utils.SettingViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var auth: FirebaseAuth
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var username: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide title bar
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign In
//        val gso = GoogleSignInOptions
//            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
//        auth = Firebase.auth
//
//        binding.signInButton.setOnClickListener {
//            signIn()
//        }

        val viewModel = obtainViewModel(this)
        val pref = SettingPreferences.getInstance((this).dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )

        binding.buttonSignin.setOnClickListener {
            username = binding.edLoginUsername.text.toString().trim()
            password = binding.edLoginPassword.text.toString().trim()

            if (TextUtils.isEmpty(username)) {
                binding.edLoginUsername.error = "Field must be filled"
            } else if (TextUtils.isEmpty(password)) {
                binding.edLoginPassword.error = "Field must be filled"
            } else if (binding.edLoginPassword.error?.length ?: 0 > 0) {
                binding.edLoginPassword.requestFocus()
            }
            else {
                viewModel.login(username, password)
                viewModel.loginUser.observe(this) { loginResponse ->
                    if (loginResponse.success) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.isLoading.observe(this) {
//            showLoading(it)
        }

        viewModel.loginUser.observe(this) { // get user login response and save to datastore
                login ->
//            val currentDate = Date()
            val currentDate = Calendar.getInstance().time
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
//            calendar.add(Calendar.HOUR_OF_DAY, 2)
            calendar.add(Calendar.MINUTE, 100)
            val expiredDate = calendar.time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val formattedDate = dateFormat.format(expiredDate)

            settingViewModel.setUserPreferences(
                login.data.accessToken,
                login.data.name,
                username,
                login.data.userid,
//                login.data.expiredAt
                formattedDate.toString()
            )
        }

        settingViewModel.getToken().observe(this) { token ->
            if (token != "Not Set") {
                val mIntent = Intent(this, MainActivity::class.java)
                startActivity(mIntent)
            }
        }
    }

//    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        resultLauncher.launch(signInIntent)
//    }
//
//    private var resultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e)
//            }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
//                }
//            }
//    }
//
//    private fun updateUI(currentUser: FirebaseUser?) {
//        if (currentUser != null){
//            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            finish()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }
    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[LoginViewModel::class.java]
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}