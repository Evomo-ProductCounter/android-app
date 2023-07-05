package com.evomo.productcounterapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.RawValue

@Parcelize
data class LoginResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("access_token")
	val accessToken: String,

	@field:SerializedName("refresh_token")
	val refreshToken: String? = null,

	@field:SerializedName("scope")
	val scope: String? = null,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("expired_at")
	val expiredAt: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("expires_in")
	val expiresIn: Int? = null,

	@field:SerializedName("userid")
	val userid: String,

	@field:SerializedName("client_id")
	val clientId: String? = null
) : Parcelable
