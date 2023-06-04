package com.evomo.productcounterapp.ui.main.home

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.data.db.MachineInfo
import com.evomo.productcounterapp.databinding.FragmentHomeBinding
import com.evomo.productcounterapp.ui.ViewModelFactory
import com.evomo.productcounterapp.ui.camera.CameraActivity
import com.evomo.productcounterapp.utils.DateHelper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

    var machineOptions = arrayOf("Machine 1", "Machine 2", "Machine 3", "Machine 4")
    var machineTextView: AutoCompleteTextView? = null
    var machineAdapterItems: ArrayAdapter<String>? = null
    private var selectedMachine: String? = null
    private lateinit var machineInfo : List<MachineInfo>
    private var In : Int = 0
    private var Out : Int = 0
    private var Reject : Int = 0
    private lateinit var pieChart : PieChart

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

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        var name = ""

        if (firebaseUser != null) {
            name = firebaseUser.displayName.toString()
        }

        binding.titleWelcome.text = resources.getString(R.string.title_welcome, name.split(" ").firstOrNull())

        binding.date.text = DateHelper.getCurrentDateNoTime()

        binding.cameraButton.setOnClickListener{
            CameraActivity.cameraWidth = 0
            CameraActivity.cameraHeight = 0
            CameraActivity.centerX = 0
            CameraActivity.centerY = 0
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

        val viewModel = obtainViewModel(activity as AppCompatActivity)

        machineTextView = binding.autocompleteMesinChart
        machineTextView!!.inputType = InputType.TYPE_NULL
        machineAdapterItems =
            context?.let { ArrayAdapter(it, R.layout.dropdown_items, machineOptions) }
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
                // Format the value as per your requirement
                return "%.1f%%".format(value)
            }
        }
        data.setValueFormatter(formatter)
        data.setValueTextSize(11f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()
    }

    private fun obtainViewModel(activity: AppCompatActivity): HomeViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HomeViewModel::class.java]
    }

    companion object {

    }
}