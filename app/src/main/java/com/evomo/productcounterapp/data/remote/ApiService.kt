package com.evomo.productcounterapp.data.remote

import com.evomo.productcounterapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("login")
    @FormUrlEncoded
    fun login (
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("machines")
    fun getMachines (
        @Header("Authorization") token: String,
    ): Call<MachinesResponse>

    @GET("machine/products")
    fun getProducts (
        @Header("Authorization") token: String,
        @Query("machine_id") machineId: String
    ): Call<MachineProductResponse>

    @GET("planned_running_time/byuser")
    fun getRuntime (
        @Header("Authorization") token: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("user_id") userId: String
    ): Call<CurrentRuntimeResponse>
}