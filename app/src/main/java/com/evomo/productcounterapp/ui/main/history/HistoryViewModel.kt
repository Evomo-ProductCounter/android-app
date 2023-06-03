package com.evomo.productcounterapp.ui.main.history

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.evomo.productcounterapp.data.db.CountObject
import com.evomo.productcounterapp.data.repository.CountRepository

class HistoryViewModel (application: Application) : ViewModel() {
    private val mCountRepository: CountRepository = CountRepository(application)

    fun getAllCount(): LiveData<List<CountObject>> = mCountRepository.getAllCount()
}