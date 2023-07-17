package com.evomo.productcounterapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class MachineProductResponse(

	@field:SerializedName("data")
	val data: List<DataProduct>,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class DataProduct(

	@field:SerializedName("product")
	val product: Product,

	@field:SerializedName("cycle_time")
	val cycleTime: Double,

	@field:SerializedName("rpm")
	val rpm: Int
) : Parcelable {
	override fun toString(): String {
		return product.name
	}
}

@Parcelize
data class Product(

	@field:SerializedName("machine_category_id")
	val machineCategoryId: List<String>,

	@field:SerializedName("total_cetakan")
	val totalCetakan: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("cycle_time")
	val cycleTime: Double,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("seri")
	val seri: String,

	@field:SerializedName("production_line")
	val productionLine: String,

	@field:SerializedName("rpm")
	val rpm: Int,

	@field:SerializedName("client_id")
	val clientId: String
) : Parcelable
