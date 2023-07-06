package com.evomo.productcounterapp.ui.main.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.evomo.productcounterapp.data.db.MachineInfo
import com.evomo.productcounterapp.data.model.Machine
import com.evomo.productcounterapp.data.model.MachinesResponse
import com.evomo.productcounterapp.data.remote.ApiConfig
import com.evomo.productcounterapp.data.repository.CountRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel (application: Application, token: String) : ViewModel() {
    private val mCountRepository: CountRepository = CountRepository(application)

    fun getMachineInfo(machineName : String): LiveData<List<MachineInfo>> = mCountRepository.getMachineInfo(machineName)

    private var token = token

    private val _listMachine = MutableLiveData<List<Machine>>()
    val listMachine: LiveData<List<Machine>> = _listMachine

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
//        getStories(0,20)
        getMachines()
    }

    fun getMachines() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getMachines(token)
        client.enqueue(object : Callback<MachinesResponse> {
            override fun onResponse(
                call: Call<MachinesResponse>,
                response: Response<MachinesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listMachine.value = response.body()?.data
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<MachinesResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object{
        private const val TAG = "HomeViewModel"
    }
}