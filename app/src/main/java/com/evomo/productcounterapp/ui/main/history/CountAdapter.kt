package com.evomo.productcounterapp.ui.main.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.data.db.CountObject
import com.evomo.productcounterapp.databinding.ItemHistoryBinding
import com.evomo.productcounterapp.utils.CountDiffCallback
import android.content.res.Resources

class CountAdapter : RecyclerView.Adapter<CountAdapter.CountViewHolder>() {
    private val listCounts = ArrayList<CountObject>()

    fun setListCounts(listCounts: List<CountObject>) {
        val diffCallback = CountDiffCallback(this.listCounts, listCounts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listCounts.clear()
        this.listCounts.addAll(listCounts)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountViewHolder, position: Int) {
        holder.bind(listCounts[position])
    }

    override fun getItemCount(): Int {
        return listCounts.size
    }

    inner class CountViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(count: CountObject) {
            with(binding) {
                itemMachine.text = count.machine
                itemCount.text = count.count.toString()
                itemParameter.text = "${count.parameter}:"
                itemDate.text = count.date
            }
        }
    }
}