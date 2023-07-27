package com.evomo.productcounterapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class OEEResponse(

	@field:SerializedName("data")
	val data: DataOEE,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class CurrentDetail(

	@field:SerializedName("product")
	val product: ProductOEE,

	@field:SerializedName("maximum_output")
	val maximumOutput: Int,

	@field:SerializedName("total_cetakan")
	val totalCetakan: String,

	@field:SerializedName("machine")
	val machine: Machine,

	@field:SerializedName("start")
	val start: String,

	@field:SerializedName("cetakan_output")
	val cetakanOutput: Int,

	@field:SerializedName("runtime_id")
	val runtimeId: String,

	@field:SerializedName("runtime")
	val runtime: Runtime,

	@field:SerializedName("cycle_time")
	val cycleTime: Double,

	@field:SerializedName("end")
	val end: String,

	@field:SerializedName("id")
	val id: String
) : Parcelable

@Parcelize
data class ProductOEE(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("seri")
	val seri: String,

	@field:SerializedName("rpm")
	val rpm: Int,

	@field:SerializedName("client_id")
	val clientId: String
) : Parcelable

@Parcelize
data class Runtime(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("start")
	val start: String,

	@field:SerializedName("machine_id")
	val machineId: String,

	@field:SerializedName("end")
	val end: String,

	@field:SerializedName("id")
	val id: String,

//	@field:SerializedName("targets")
//	val targets: Any,

	@field:SerializedName("client_id")
	val clientId: String
) : Parcelable

@Parcelize
data class DataOEE(
	@field:SerializedName("data")
	val data: OEENumbers,

	@field:SerializedName("current_runtime_info")
	val currentRuntimeInfo: List<CurrentRuntimeInfoItem>,
) : Parcelable


@Parcelize
data class OEENumbers(
	@field:SerializedName("performance")
	val performance: Double,

	@field:SerializedName("utility")
	val utility: Int,

	@field:SerializedName("teep")
	val teep: Double,

	@field:SerializedName("availability")
	val availability: Double,

	@field:SerializedName("oee")
	val oee: Double,

	@field:SerializedName("quality")
	val quality: Double
) : Parcelable

@Parcelize
data class MachineOEE(

	@field:SerializedName("machine_category_id")
	val machineCategoryId: List<String>,

	@field:SerializedName("sector_id")
	val sectorId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("production_line")
	val productionLine: String,

	@field:SerializedName("client_id")
	val clientId: String,

	@field:SerializedName("status")
	val status: Boolean,

	@field:SerializedName("user_id")
	val userId: String
) : Parcelable

@Parcelize
data class CurrentRuntimeInfoItem(

	@field:SerializedName("user_id")
	val userId: List<String>,

	@field:SerializedName("machine")
	val machine: MachineOEE,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("start")
	val start: String,

	@field:SerializedName("current_detail")
	val currentDetail: CurrentDetail,

	@field:SerializedName("end")
	val end: String,

	@field:SerializedName("id")
	val id: String
) : Parcelable
