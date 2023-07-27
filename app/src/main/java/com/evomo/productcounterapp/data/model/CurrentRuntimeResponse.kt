package com.evomo.productcounterapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class CurrentRuntimeResponse(

	@field:SerializedName("data")
	val data: DataRuntime,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class MachineRuntime(

	@field:SerializedName("machine_category_id")
	val machineCategoryId: List<String>,

	@field:SerializedName("user_id")
	val userId: String,

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
	val status: Boolean
) : Parcelable

@Parcelize
data class CurrentRuntimeItem(

	@field:SerializedName("detail_runtimes")
	val detailRuntimes: List<DetailRuntimesItem>,

	@field:SerializedName("user_id")
	val userId: List<String>,

	@field:SerializedName("machine")
	val machine: MachineRuntime,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("start")
	val start: String,

	@field:SerializedName("end")
	val end: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("targets")
	val targets: List<TargetsItem>
) : Parcelable

@Parcelize
data class DataRuntime(
//
//	@field:SerializedName("passed_runtime")
//	val passedRuntime: Any,

	@field:SerializedName("current_runtime")
	val currentRuntime: List<CurrentRuntimeItem>,

	@field:SerializedName("next_runtime")
	val nextRuntime: List<NextRuntimeItem>
) : Parcelable

@Parcelize
data class TargetsItem(

	@field:SerializedName("value")
	val value: String,

	@field:SerializedName("key")
	val key: String
) : Parcelable

@Parcelize
data class DetailRuntimesItem(

	@field:SerializedName("product")
	val product: ProductRuntime,

	@field:SerializedName("maximum_output")
	val maximumOutput: Int,

	@field:SerializedName("total_cetakan")
	val totalCetakan: String,

	@field:SerializedName("start")
	val start: String,

	@field:SerializedName("cetakan_output")
	val cetakanOutput: Int,

	@field:SerializedName("cycle_time")
	val cycleTime: Double,

	@field:SerializedName("end")
	val end: String,

	@field:SerializedName("id")
	val id: String
) : Parcelable

@Parcelize
data class ProductRuntime(

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
data class NextRuntimeItem(

	@field:SerializedName("detail_runtimes")
	val detailRuntimes: List<DetailRuntimesItem>,

	@field:SerializedName("user_id")
	val userId: List<String>,

	@field:SerializedName("machine")
	val machine: MachineRuntime,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("start")
	val start: String,

	@field:SerializedName("end")
	val end: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("targets")
	val targets: List<TargetsItem>
) : Parcelable
