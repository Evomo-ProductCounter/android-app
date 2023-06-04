package com.evomo.productcounterapp.ui.main.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.evomo.productcounterapp.data.db.MachineInfo
import com.evomo.productcounterapp.data.repository.CountRepository

class HomeViewModel (application: Application) : ViewModel() {
    private val mCountRepository: CountRepository = CountRepository(application)

    fun getMachineInfo(machineName : String): LiveData<List<MachineInfo>> = mCountRepository.getMachineInfo(machineName)
}