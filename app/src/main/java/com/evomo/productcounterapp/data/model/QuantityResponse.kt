package com.evomo.productcounterapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class QuantityResponse(

	@field:SerializedName("data")
	val data: DataQuantity,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class DetailItem(

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("defect")
	val defect: Int,

	@field:SerializedName("machine_id")
	val machineId: String,

	@field:SerializedName("time")
	val time: String,

	@field:SerializedName("rework")
	val rework: Int,

	@field:SerializedName("good")
	val good: Int
) : Parcelable

@Parcelize
data class DataQuantity(

	@field:SerializedName("data")
	val data: QuantityNumbers,

	@field:SerializedName("current_runtime_info")
	val currentRuntimeInfo: List<CurrentRuntimeInfoItem>,
) : Parcelable

@Parcelize
data class QuantityNumbers(
	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("defect")
	val defect: Int,

	@field:SerializedName("rework")
	val rework: Int,

	@field:SerializedName("detail")
	val detail: List<DetailItem>,

	@field:SerializedName("good")
	val good: Int
) : Parcelable
