package com.evomo.productcounterapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evomo.productcounterapp.data.model.AgreeTnCResponse
import com.evomo.productcounterapp.data.model.LoginResponse
import com.evomo.productcounterapp.data.remote.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val emailTemp = MutableLiveData("")

    private val _loginUser = MutableLiveData<LoginResponse>()
    val loginUser: LiveData<LoginResponse> = _loginUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isAgree = MutableLiveData<AgreeTnCResponse>()
    val isAgree: LiveData<AgreeTnCResponse> = _isAgree

    fun login(email: String, password: String) {
        _isLoading.value = true
        _isError.value = false
        emailTemp.value = email
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginUser.value = response.body()
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun agreeTnC(userId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().isAgree(userId)
        client.enqueue(object : Callback<AgreeTnCResponse> {
            override fun onResponse(
                call: Call<AgreeTnCResponse>,
                response: Response<AgreeTnCResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _isAgree.value = response.body()
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<AgreeTnCResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }
}