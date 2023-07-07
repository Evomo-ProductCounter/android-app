package com.evomo.productcounterapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class MachinesResponse(

	@field:SerializedName("data")
	val data: List<Machine>,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class Machine(

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
) : Parcelable {
	override fun toString(): String {
		return name
	}
}
