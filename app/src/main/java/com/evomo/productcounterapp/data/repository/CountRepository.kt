package com.evomo.productcounterapp.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.evomo.productcounterapp.data.db.CountObject
import com.evomo.productcounterapp.data.db.CountObjectDao
import com.evomo.productcounterapp.data.db.CountObjectRoomDb
import com.evomo.productcounterapp.data.db.MachineInfo
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CountRepository (application: Application) {
    private val mCountObjectDao : CountObjectDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = CountObjectRoomDb.getDatabase(application)
        mCountObjectDao = db.countObjectDao()
    }
    fun getAllCount(): LiveData<List<CountObject>> = mCountObjectDao.getAllCountByDate()

    fun getMachineInfo(machineName : String): LiveData<List<MachineInfo>> = mCountObjectDao.getMachineInfo(machineName)

    fun insert(countObject: CountObject) {
        executorService.execute { mCountObjectDao.insert(countObject) }
    }

    fun delete(countObject: CountObject) {
        executorService.execute { mCountObjectDao.delete(countObject) }
    }

    fun update(countObject: CountObject) {
        executorService.execute { mCountObjectDao.update(countObject) }
    }
}