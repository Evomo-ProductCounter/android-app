package com.evomo.productcounterapp.data.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class CountObject (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "machine")
    var machine: String? = null,

    @ColumnInfo(name = "parameter")
    var parameter: String? = null,

    @ColumnInfo(name = "count")
    var count: Int? = null,

    @ColumnInfo(name = "date")
    var date: String? = null
) : Parcelable