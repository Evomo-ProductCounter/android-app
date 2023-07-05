package com.evomo.productcounterapp.data.remote

import com.evomo.productcounterapp.data.model.LoginResponse
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
}