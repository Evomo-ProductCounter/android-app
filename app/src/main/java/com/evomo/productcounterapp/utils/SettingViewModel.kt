package com.evomo.productcounterapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel (private val pref: SettingPreferences) : ViewModel() {
    fun getUserName(): LiveData<String> {
        return pref.getName().asLiveData()
    }

    fun getToken(): LiveData<String> {
        return pref.getUserToken().asLiveData()
    }

    fun getUID(): LiveData<String> {
        return pref.getUID().asLiveData()
    }

    fun getUserUsername(): LiveData<String> {
        return pref.getUserName().asLiveData()
    }

    fun setUserPreferences(userToken: String, userName:String, userUsername: String, userUID: String ) {
        viewModelScope.launch {
            pref.saveLoginSession(userToken, userName, userUsername, userUID)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            pref.deleteLoginSession()
        }
    }

}