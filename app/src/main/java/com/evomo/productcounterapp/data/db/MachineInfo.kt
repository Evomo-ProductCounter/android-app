package com.evomo.productcounterapp.data.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MachineInfo (
    var machine: String? = null,
    var parameter: String? = null,
    var total: Int? = null,
) : Parcelable