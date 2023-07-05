package com.evomo.productcounterapp.ui.main.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.evomo.productcounterapp.databinding.FragmentHistoryBinding
import com.evomo.productcounterapp.ui.ViewModelFactory

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: CountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = obtainViewModel(activity as AppCompatActivity)
        viewModel.getAllCount().observe(viewLifecycleOwner) { list ->
            if (list != null) {
                adapter.setListCounts(list)
            }
        }

        adapter = CountAdapter()
        binding?.rvHistory?.layoutManager = LinearLayoutManager(context)
        binding?.rvHistory?.setHasFixedSize(true)
        binding?.rvHistory?.adapter = adapter

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HistoryViewModel::class.java]
    }
}