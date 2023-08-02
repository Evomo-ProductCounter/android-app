package com.evomo.productcounterapp.ui.main.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.data.db.MachineInfo
import com.evomo.productcounterapp.data.model.Machine
import com.evomo.productcounterapp.databinding.FragmentHomeBinding
import com.evomo.productcounterapp.ui.TokenViewModelFactory
import com.evomo.productcounterapp.ui.ViewModelFactory
import com.evomo.productcounterapp.ui.camera.CameraActivity
import com.evomo.productcounterapp.ui.login.LoginActivity
import com.evomo.productcounterapp.ui.main.MainActivity
import com.evomo.productcounterapp.utils.DateHelper
import com.evomo.productcounterapp.utils.SettingPreferences
import com.evomo.productcounterapp.utils.SettingViewModel
import com.evomo.productcounterapp.utils.SettingViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var nameList: List<String>
    private lateinit var machineList: List<Machine>

    var machineTextView: AutoCompleteTextView? = null
    var machineAdapterItems: ArrayAdapter<String>? = null
    private var selectedMachine: String? = null
    private lateinit var machineInfo : List<MachineInfo>
    private var In : Int = 0
    private var Out : Int = 0
    private var Reject : Int = 0
    private lateinit var pieChart : PieChart
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreferences.getInstance((activity as MainActivity).dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )

        settingViewModel.getUserName().observe(viewLifecycleOwner) { dataName ->
            userName = dataName
            binding.titleWelcome.text = resources.getString(R.string.title_welcome, dataName.split(" ").firstOrNull())
        }

        settingViewModel.getToken().observe(viewLifecycleOwner) { token ->
            CameraActivity.mToken = token
        }

        binding.date.text = DateHelper.getCurrentDateNoTime()

        binding.cameraButton.setOnClickListener{
            CameraActivity.cameraWidth = 0
            CameraActivity.cameraHeight = 0
            CameraActivity.centerX = 0
            CameraActivity.centerY = 0
            CameraActivity.machineOptions = nameList.toTypedArray()
            CameraActivity.machinesList = machineList.toTypedArray()
            CameraActivity.userName = userName
            val intent = Intent(activity, CameraActivity::class.java)
            startActivity(intent)
        }

        pieChart = binding.pieChart
        val entries: ArrayList<PieEntry> = ArrayList()

        //initialize empty info
        entries.add(PieEntry(0F))
        entries.add(PieEntry(0F))
        entries.add(PieEntry(0F))
        binding.totalNum.text = (In + Out + Reject).toString()
        binding.inOutNum.text = (In + Out).toString()
        binding.rejectNum.text = Reject.toString()
        setChart(entries)

        settingViewModel.getToken().observe(viewLifecycleOwner) { token ->
            Log.d("TOKEN CHECK", token)
            if (token == "Not Set") {
                startActivity(Intent(activity, LoginActivity::class.java))
            }

            val viewModel by viewModels<HomeViewModel>(){
                TokenViewModelFactory((activity as MainActivity).application, token)
            }

            viewModel.getMachines()

            viewModel.isLoading.observe(activity as AppCompatActivity) { loading ->
                showLoading(loading)
            }

            machineTextView = binding.autocompleteMesinChart
            machineTextView!!.inputType = InputType.TYPE_NULL

            viewModel.listMachine.observe(activity as AppCompatActivity) { list ->
                if (list.isEmpty()) {
                    viewModel.getMachines()
                }
                else {
                    machineList = list
                    nameList = list.map { item ->
                        item.name
                    }

                    machineAdapterItems =
                        context?.let { ArrayAdapter(it, R.layout.dropdown_items, nameList) }
                    machineTextView!!.setAdapter(machineAdapterItems)
                    machineTextView!!.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, view, position, id ->
                            val item = parent.getItemAtPosition(position).toString()
                            selectedMachine = item
                            viewModel.getMachineInfo(selectedMachine!!).observe(viewLifecycleOwner) { list ->
                                if (list != null) {
                                    In = list.find {
                                        it.parameter == "In"
                                    }?.total ?: 0
                                    Out = list.find {
                                        it.parameter == "Out"
                                    }?.total ?: 0
                                    Reject = list.find {
                                        it.parameter == "Reject"
                                    }?.total ?: 0

                                    entries.clear()
                                    if (In != null) {
                                        entries.add(PieEntry(In.toFloat()))
                                    }
                                    if (Out != null) {
                                        entries.add(PieEntry(Out.toFloat()))
                                    }
                                    if (Reject != null) {
                                        entries.add(PieEntry(Reject.toFloat()))
                                    }
                                    binding.totalNum.text = (In + Out + Reject).toString()
                                    binding.inOutNum.text = (In + Out).toString()
                                    binding.rejectNum.text = Reject.toString()
                                    setChart(entries)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun setChart(entries: ArrayList<PieEntry>) {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.dragDecelerationFrictionCoef = 0.95f

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f

        pieChart.setDrawCenterText(true)

        pieChart.rotationAngle = 0f

        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        pieChart.animateY(1400, Easing.EaseInOutQuad)

        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        val dataSet = PieDataSet(entries, "Product Count")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.green_500))
        colors.add(resources.getColor(R.color.orange_500))
        colors.add(resources.getColor(R.color.red))
        dataSet.colors = colors

        val data = PieData(dataSet)
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "%.1f%%".format(value)
            }
        }
        data.setValueFormatter(formatter)
        data.setValueTextSize(11f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        pieChart.highlightValues(null)

        pieChart.invalidate()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity, token: String): HomeViewModel {
        val factory = TokenViewModelFactory.getInstance(activity.application, token)
        return ViewModelProvider(activity, factory)[HomeViewModel::class.java]
    }
}