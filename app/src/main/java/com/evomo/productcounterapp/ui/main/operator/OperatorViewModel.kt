package com.evomo.productcounterapp.ui.main.operator

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evomo.productcounterapp.data.model.*
import com.evomo.productcounterapp.data.remote.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OperatorViewModel (application: Application, token: String) : ViewModel() {
    private val _socketStatus = MutableLiveData(false)
    val socketStatus: LiveData<Boolean> = _socketStatus

    private val _oee = MutableLiveData<DataOEE>()
    val oee: LiveData<DataOEE> = _oee

    private val _quantity = MutableLiveData<DataQuantity>()
    val quantity: LiveData<DataQuantity> = _quantity

    private val _downtime = MutableLiveData<DataDowntime>()
    val downtime: LiveData<DataDowntime> = _downtime

    fun addOEEData(data: DataOEE) = viewModelScope.launch(Dispatchers.Main) {
        if (_socketStatus.value == true) {
            _oee.value = data
        }
    }

    fun addQuantityData(data: DataQuantity) = viewModelScope.launch(Dispatchers.Main) {
        if (_socketStatus.value == true) {
            _quantity.value = data
        }
    }

    fun addDowntimeData(data: DataDowntime) = viewModelScope.launch(Dispatchers.Main) {
        if (_socketStatus.value == true) {
            _downtime.value = data
        }
    }

    fun setStatus(status: Boolean) = viewModelScope.launch(Dispatchers.Main) {
        _socketStatus.value = status
    }

    private var token = token

    private val _listRuntime = MutableLiveData<DataRuntime>()
    val listRuntime: LiveData<DataRuntime> = _listRuntime

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getRuntime(userId: String) {
        _isLoading.value = true
//        val startDate = LocalDate.now()
//        val endDate = startDate.plusDays(1)
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedStartDate = startDate.format(formatter)
        val formattedEndDate = endDate.format(formatter)

        val client = ApiConfig.getApiService().getRuntime(token, formattedStartDate, formattedEndDate, userId)
        client.enqueue(object : Callback<CurrentRuntimeResponse> {
            override fun onResponse(
                call: Call<CurrentRuntimeResponse>,
                response: Response<CurrentRuntimeResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    Log.d("TEST", response.toString())
                    _listRuntime.value = response.body()?.data
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<CurrentRuntimeResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object{
        private const val TAG = "OperatorViewModel"
    }
}