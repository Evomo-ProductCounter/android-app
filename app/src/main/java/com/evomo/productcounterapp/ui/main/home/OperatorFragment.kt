package com.evomo.productcounterapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekn.gruzer.gaugelibrary.Range
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.FragmentOperatorBinding
import com.evomo.productcounterapp.ui.main.customview.CircularProgressBar


class OperatorFragment : Fragment() {
    private lateinit var binding: FragmentOperatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val range = Range()
        range.color = ContextCompat.getColor(requireContext(), R.color.red)
        range.from = 0.0
        range.to = 50.0

        val range2 = Range()
        range2.color = ContextCompat.getColor(requireContext(), R.color.orange_500)
        range2.from = 50.0
        range2.to = 100.0

        val range3 = Range()
        range3.color = ContextCompat.getColor(requireContext(), R.color.green_700)
        range3.from = 100.0
        range3.to = 150.0

        var halfGauge = binding.halfGauge
        //add color ranges to gauge
        halfGauge.addRange(range)
        halfGauge.addRange(range2)
        halfGauge.addRange(range3)

        //set min max and current value
        halfGauge.minValue = 0.0
        halfGauge.maxValue = 150.0
        halfGauge.value = 73.4



        ////

        val availPercent: CircularProgressBar = binding.availPercentage
        availPercent.setProgress(97)

        val performancePercent: CircularProgressBar = binding.performancePercentage
        performancePercent.setProgress(80)

        val qualityPercent: CircularProgressBar = binding.qualityPercentage
        qualityPercent.setProgress(94)

//        val gaugeChart: PieChart = binding.gaugeChart

        // Set the data for the pie chart

        // Set the data for the pie chart
//        val entries: MutableList<PieEntry> = ArrayList()
//        entries.add(PieEntry(40f, "")) // Replace 40f with your first data value
//        entries.add(PieEntry(30f, "")) // Replace 30f with your second data value
//        entries.add(PieEntry(30f, "")) // Replace 30f with your third data value
//
//        val dataSet = PieDataSet(entries, "")
////        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
//        val colors = ArrayList<Int>()
//        colors.add(ContextCompat.getColor(requireContext(), R.color.red)) // Replace with your first color
//        colors.add(ContextCompat.getColor(requireContext(), R.color.orange_500)) // Replace with your second color
//        colors.add(ContextCompat.getColor(requireContext(), R.color.green_700)) // Replace with your third color
//        dataSet.colors = colors
//        dataSet.sliceSpace = 0f
//        dataSet.setDrawValues(false)
//
//        val data = PieData(dataSet)
//        gaugeChart.data = data
//
//        // Hide unnecessary chart elements
//        val description = Description()
//        description.text = ""
//        gaugeChart.description = description
//
//        gaugeChart.isDrawHoleEnabled = true
//        gaugeChart.setHoleColor(Color.TRANSPARENT)
//        gaugeChart.holeRadius = 70f // Adjust this value to make the hole smaller or bigger
//
//        gaugeChart.isRotationEnabled = false
//        gaugeChart.legend.isEnabled = false
//        gaugeChart.setDrawEntryLabels(false)
//
//        // Set the angle at which the pie chart should start and the range to show only the top half
//        gaugeChart.rotationAngle = 180f
//        gaugeChart.maxAngle = 180f
//
//        // Disable interactions with the chart
//        gaugeChart.setTouchEnabled(false)
//        gaugeChart.isDragDecelerationEnabled = false
//        gaugeChart.isHighlightPerTapEnabled = false
//
//        // Calculate the angle for the value you want to highlight
//        val valueToHighlight = 2f // Replace with the value you want to highlight
//        val angleToHighlight = calculateAngle(valueToHighlight)
//
//        val markerView = CustomMarkerView(gaugeChart.context, R.layout.custom_marker_view, angleToHighlight)
//        gaugeChart.marker = markerView
//
//        gaugeChart.invalidate()

//        val bulletChartView = binding.bulletChartContainer
//        bulletChartView.setMaxValue(100f)
//        bulletChartView.setCurrentValue(55f)
//        bulletChartView.setTargetValue(80f)
//        bulletChartView.setPerformanceRange(60f, 80f)

//
//        val anyChartView: AnyChartView = binding.anyChartView
//
//        val circularGauge = AnyChart.circular()
//        circularGauge.data(SingleValueDataSet(arrayOf("23", "34", "67", "93", "56", "100")))
//        circularGauge.fill("#fff")
//            .stroke(null)
//            .padding(0.0, 0.0, 0.0, 0.0)
//            .margin(100.0, 100.0, 100.0, 100.0)
//        circularGauge.startAngle(0.0)
//        circularGauge.sweepAngle(270.0)
//
//        val xAxis = circularGauge.axis(0)
//            .radius(100.0)
//            .width(1.0)
//            .fill(null as Fill?)
//        xAxis.scale()
//            .minimum(0.0)
//            .maximum(100.0)
//        xAxis.ticks("{ interval: 1 }")
//            .minorTicks("{ interval: 1 }")
//        xAxis.labels().enabled(false)
//        xAxis.ticks().enabled(false)
//        xAxis.minorTicks().enabled(false)
//
//        circularGauge.label(0.0)
//            .text("Temazepam, <span style=\"\">32%</span>")
//            .useHtml(true)
//            .hAlign(HAlign.CENTER)
//            .vAlign(VAlign.MIDDLE)
//        circularGauge.label(0.0)
//            .anchor(Anchor.RIGHT_CENTER)
//            .padding(0.0, 10.0, 0.0, 0.0)
//            .height(17.0 / 2.0)
//            .offsetY(100.0.toString() + "%")
//            .offsetX(0.0)
//        val bar0: Bar = circularGauge.bar(0.0)
//        bar0.dataIndex(0.0)
//        bar0.radius(100.0)
//        bar0.width(17.0)
//        bar0.fill(SolidFill("#64b5f6", 1.0))
//        bar0.stroke(null)
//        bar0.zIndex(5.0)
//        val bar100: Bar = circularGauge.bar(100.0)
//        bar100.dataIndex(5.0)
//        bar100.radius(100.0)
//        bar100.width(17.0)
//        bar100.fill(SolidFill("#F5F4F4", 1.0))
//        bar100.stroke("1 #e5e4e4")
//        bar100.zIndex(4.0)
//
//        circularGauge.label(1.0)
//            .text("Guaifenesin, <span style=\"\">34%</span>")
//            .useHtml(true)
//            .hAlign(HAlign.CENTER)
//            .vAlign(VAlign.MIDDLE)
//        circularGauge.label(1.0)
//            .anchor(Anchor.RIGHT_CENTER)
//            .padding(0.0, 10.0, 0.0, 0.0)
//            .height(17.0 / 2.0)
//            .offsetY(80.0.toString() + "%")
//            .offsetX(0.0)
//        val bar1: Bar = circularGauge.bar(1.0)
//        bar1.dataIndex(1.0)
//        bar1.radius(80.0)
//        bar1.width(17.0)
//        bar1.fill(SolidFill("#1976d2", 1.0))
//        bar1.stroke(null)
//        bar1.zIndex(5.0)
//        val bar101: Bar = circularGauge.bar(101.0)
//        bar101.dataIndex(5.0)
//        bar101.radius(80.0)
//        bar101.width(17.0)
//        bar101.fill(SolidFill("#F5F4F4", 1.0))
//        bar101.stroke("1 #e5e4e4")
//        bar101.zIndex(4.0)
//
//        circularGauge.label(2.0)
//            .text("Salicylic Acid, <span style=\"\">67%</span>")
//            .useHtml(true)
//            .hAlign(HAlign.CENTER)
//            .vAlign(VAlign.MIDDLE)
//        circularGauge.label(2.0)
//            .anchor(Anchor.RIGHT_CENTER)
//            .padding(0.0, 10.0, 0.0, 0.0)
//            .height(17.0 / 2.0)
//            .offsetY(60.0.toString() + "%")
//            .offsetX(0.0)
//        val bar2: Bar = circularGauge.bar(2.0)
//        bar2.dataIndex(2.0)
//        bar2.radius(60.0)
//        bar2.width(17.0)
//        bar2.fill(SolidFill("#ef6c00", 1.0))
//        bar2.stroke(null)
//        bar2.zIndex(5.0)
//        val bar102: Bar = circularGauge.bar(102.0)
//        bar102.dataIndex(5.0)
//        bar102.radius(60.0)
//        bar102.width(17.0)
//        bar102.fill(SolidFill("#F5F4F4", 1.0))
//        bar102.stroke("1 #e5e4e4")
//        bar102.zIndex(4.0)
//
//        circularGauge.label(3.0)
//            .text("Fluoride, <span style=\"\">93%</span>")
//            .useHtml(true)
//            .hAlign(HAlign.CENTER)
//            .vAlign(VAlign.MIDDLE)
//        circularGauge.label(3.0)
//            .anchor(Anchor.RIGHT_CENTER)
//            .padding(0.0, 10.0, 0.0, 0.0)
//            .height(17.0 / 2.0)
//            .offsetY(40.0.toString() + "%")
//            .offsetX(0.0)
//        val bar3: Bar = circularGauge.bar(3.0)
//        bar3.dataIndex(3.0)
//        bar3.radius(40.0)
//        bar3.width(17.0)
//        bar3.fill(SolidFill("#ffd54f", 1.0))
//        bar3.stroke(null)
//        bar3.zIndex(5.0)
//        val bar103: Bar = circularGauge.bar(103.0)
//        bar103.dataIndex(5.0)
//        bar103.radius(40.0)
//        bar103.width(17.0)
//        bar103.fill(SolidFill("#F5F4F4", 1.0))
//        bar103.stroke("1 #e5e4e4")
//        bar103.zIndex(4.0)
//
//        circularGauge.label(4.0)
//            .text("Zinc Oxide, <span style=\"\">56%</span>")
//            .useHtml(true)
//            .hAlign(HAlign.CENTER)
//            .vAlign(VAlign.MIDDLE)
//        circularGauge.label(4.0)
//            .anchor(Anchor.RIGHT_CENTER)
//            .padding(0.0, 10.0, 0.0, 0.0)
//            .height(17.0 / 2.0)
//            .offsetY(20.0.toString() + "%")
//            .offsetX(0.0)
//        val bar4: Bar = circularGauge.bar(4.0)
//        bar4.dataIndex(4.0)
//        bar4.radius(20.0)
//        bar4.width(17.0)
//        bar4.fill(SolidFill("#455a64", 1.0))
//        bar4.stroke(null)
//        bar4.zIndex(5.0)
//        val bar104: Bar = circularGauge.bar(104.0)
//        bar104.dataIndex(5.0)
//        bar104.radius(20.0)
//        bar104.width(17.0)
//        bar104.fill(SolidFill("#F5F4F4", 1.0))
//        bar104.stroke("1 #e5e4e4")
//        bar104.zIndex(4.0)
//
//        circularGauge.margin(50.0, 50.0, 50.0, 50.0)
//        circularGauge.title()
//            .text(
//                """Medicine manufacturing progress' +
//    '<br/><span style="color:#929292; font-size: 12px;">(ACME CORPORATION)</span>"""
//            )
//            .useHtml(true)
//        circularGauge.title().enabled(true)
//        circularGauge.title().hAlign(HAlign.CENTER)
//        circularGauge.title()
//            .padding(0.0, 0.0, 0.0, 0.0)
//            .margin(0.0, 0.0, 20.0, 0.0)
//
//        anyChartView.setChart(circularGauge)
    }

    private fun calculateAngle(valueToHighlight: Float): Float {
        // Calculate the angle for the value to highlight
        val totalValue = 40f + 30f + 30f // Sum of all data values
        val anglePerValue = 180f / totalValue
        return 270f - anglePerValue * valueToHighlight
    }
}