package com.evomo.productcounterapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.evomo.productcounterapp.data.db.CountObject

class CountDiffCallback (private val oldCountList: List<CountObject>, private val newCountList: List<CountObject>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldCountList.size
    override fun getNewListSize(): Int = newCountList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCountList[oldItemPosition].id == newCountList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldObj = oldCountList[oldItemPosition]
        val newObj = newCountList[newItemPosition]
        return oldObj.machine == newObj.machine && oldObj.date == newObj.date && oldObj.parameter == newObj.parameter
    }
}