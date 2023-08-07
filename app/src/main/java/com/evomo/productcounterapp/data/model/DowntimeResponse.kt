package com.evomo.productcounterapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class DowntimeResponse(

	@field:SerializedName("data")
	val data: DataDowntime,

	@field:SerializedName("meta")
	val meta: Meta,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class DataDowntime(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("current_runtime_info")
	val currentRuntimeInfo: List<CurrentRuntimeInfoItem>
) : Parcelable

@Parcelize
data class Meta(

	@field:SerializedName("totalData")
	val totalData: Int,

	@field:SerializedName("total_empty_reason")
	val totalEmptyReason: Int
) : Parcelable

@Parcelize
data class Category(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("production_line")
	val productionLine: String,

	@field:SerializedName("client_id")
	val clientId: String
) : Parcelable

@Parcelize
data class ReasonDetail(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("category")
	val category: Category,

	@field:SerializedName("production_line")
	val productionLine: String,

	@field:SerializedName("client_id")
	val clientId: String
) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("duration")
	val duration: Double,

	@field:SerializedName("reason_detail")
	val reasonDetail: ReasonDetail,

	@field:SerializedName("start_time")
	val startTime: String,

	@field:SerializedName("reason")
	val reason: String,

	@field:SerializedName("note")
	val note: String,

	@field:SerializedName("end_time")
	val endTime: String,

	@field:SerializedName("machine_id")
	val machineId: String,

	@field:SerializedName("type")
	val type: String
) : Parcelable
