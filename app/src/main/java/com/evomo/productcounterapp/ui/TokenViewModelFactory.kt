package com.evomo.productcounterapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evomo.productcounterapp.ui.login.LoginViewModel
import com.evomo.productcounterapp.ui.main.history.HistoryViewModel
import com.evomo.productcounterapp.ui.main.home.HomeViewModel

class TokenViewModelFactory(private val mApplication: Application, private val token:String) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: TokenViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application, token: String): TokenViewModelFactory {
            if (INSTANCE == null) {
                synchronized(TokenViewModelFactory::class.java) {
                    INSTANCE = TokenViewModelFactory(application, token)
                }
            }
            return INSTANCE as TokenViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(mApplication, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}