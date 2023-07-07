package com.evomo.productcounterapp.data.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evomo.productcounterapp.data.model.Machine
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class CountObject (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "machine")
    var machine: String? = null,

    @ColumnInfo(name = "machineId")
    var machineId: String? = null,

    @ColumnInfo(name = "parameter")
    var parameter: String? = null,

    @ColumnInfo(name = "count")
    var count: Int? = null,

    @ColumnInfo(name = "operator")
    var operator: String? = null,

    @ColumnInfo(name = "date")
    var date: String? = null,

    @ColumnInfo(name = "speed", defaultValue = "0")
    var speed: Long? = null
) : Parcelable {
    override fun toString(): String {
        return machine!!
    }
}