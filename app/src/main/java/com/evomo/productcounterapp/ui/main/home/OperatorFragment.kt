package com.evomo.productcounterapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.SingleValueDataSet
import com.anychart.charts.LinearGauge
import com.anychart.enums.*
import com.anychart.scales.OrdinalColor
import com.ekn.gruzer.gaugelibrary.Range
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.databinding.FragmentOperatorBinding
import com.evomo.productcounterapp.ui.main.customview.BulletChartView
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

        val availPercent: CircularProgressBar = binding.availPercentage
        availPercent.setProgress(97)

        val performancePercent: CircularProgressBar = binding.performancePercentage
        performancePercent.setProgress(80)

        val qualityPercent: CircularProgressBar = binding.qualityPercentage
        qualityPercent.setProgress(94)

//        val anyChartView: AnyChartView = binding.anyChartView
//        val linearGauge: LinearGauge = AnyChart.linear()
//
//        linearGauge.data(SingleValueDataSet(arrayOf(5.3)))
//
//        linearGauge.layout(Layout.HORIZONTAL)
//
//        linearGauge.label(0)
//            .position(Position.LEFT_CENTER)
//            .anchor(Anchor.LEFT_CENTER)
//            .offsetY("-50px")
//            .offsetX("50px")
//            .fontColor("black")
//            .fontSize(17)
//        linearGauge.label(0).text("Total Rainfall")
//
//        linearGauge.label(1)
//            .position(Position.LEFT_CENTER)
//            .anchor(Anchor.LEFT_CENTER)
//            .offsetY("40px")
//            .offsetX("50px")
//            .fontColor("#777777")
//            .fontSize(17)
//        linearGauge.label(1).text("Drought Hazard")
//
//        linearGauge.label(2)
//            .position(Position.RIGHT_CENTER)
//            .anchor(Anchor.RIGHT_CENTER)
//            .offsetY("40px")
//            .offsetX("50px")
//            .fontColor("#777777")
//            .fontSize(17)
//        linearGauge.label(2).text("Flood Hazard")
//
//        val scaleBarColorScale = OrdinalColor.instantiate()
//        scaleBarColorScale.ranges(
//            arrayOf(
//                "{ from: 0, to: 2, color: ['red 0.5'] }",
//                "{ from: 2, to: 3, color: ['yellow 0.5'] }",
//                "{ from: 3, to: 7, color: ['green 0.5'] }",
//                "{ from: 7, to: 8, color: ['yellow 0.5'] }",
//                "{ from: 8, to: 10, color: ['red 0.5'] }"
//            )
//        )
//
//        linearGauge.scaleBar(0)
//            .width("5%")
//            .colorScale(scaleBarColorScale)
//
//        linearGauge.marker(0)
//            .type(MarkerType.TRIANGLE_DOWN)
//            .color("red")
//            .offset("-3.5%")
//            .zIndex(10)
//
//        linearGauge.scale()
//            .minimum(0)
//            .maximum(10)
////        linearGauge.scale().ticks
//
//        //        linearGauge.scale().ticks
//        linearGauge.axis(0)
//            .minorTicks(false)
//            .width("1%")
//        linearGauge.axis(0)
//            .offset("-1.5%")
//            .orientation(Orientation.TOP)
//            .labels("top")
//
//        linearGauge.padding(0, 30, 0, 30)
//
//        anyChartView.setChart(linearGauge)

        val bulletChartView: BulletChartView = binding.bulletChart
        bulletChartView.targetValue = 200f
        bulletChartView.currentValue = 150f
        bulletChartView.comparativeValue = 100f

        val tableLayout = binding.tableLayout
        addHeader(tableLayout)
        // Add rows dynamically
        addRowToTable(tableLayout, "07:00:00", "Downtime 1", "00:08:00")
        addRowToTable(tableLayout, "07:08:00", "Downtime 2", "00:00:30")
        addRowToTable(tableLayout, "07:53:00", "Downtime 3", "00:01:00")
    }

    private fun calculateAngle(valueToHighlight: Float): Float {
        // Calculate the angle for the value to highlight
        val totalValue = 40f + 30f + 30f // Sum of all data values
        val anglePerValue = 180f / totalValue
        return 270f - anglePerValue * valueToHighlight
    }

    private fun addRowToTable(tableLayout: TableLayout, item1: String, item2: String, item3: String) {
        val tableRow = TableRow(activity)
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT,
        )
        tableRow.layoutParams = layoutParams
        tableRow.setPadding(24, 8, 8, 16)

        val textView1 = TextView(activity)
        textView1.text = item1
        textView1.setTextAppearance(R.style.TableColumn)

        val textView2 = TextView(activity)
        textView2.text = item2
        textView2.setTextAppearance(R.style.TableColumn)

        val textView3 = TextView(activity)
        textView3.text = item3
        textView3.setTextAppearance(R.style.TableColumn)

        tableRow.addView(textView1)
        tableRow.addView(textView2)
        tableRow.addView(textView3)

        tableLayout.addView(tableRow)
    }

    private fun addHeader(tableLayout: TableLayout) {
        val tableRow = TableRow(activity)
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT,
        )
        tableRow.layoutParams = layoutParams
        tableRow.setPadding(24, 24, 8, 16)

        val textView1 = TextView(activity)
        textView1.text = getString(R.string.stop_time)
        textView1.setTextAppearance(R.style.TableHeader)

        val textView2 = TextView(activity)
        textView2.text = getString(R.string.downtime)
        textView2.setTextAppearance(R.style.TableHeader)

        val textView3 = TextView(activity)
        textView3.text = getString(R.string.time)
        textView3.setTextAppearance(R.style.TableHeader)

        tableRow.addView(textView1)
        tableRow.addView(textView2)
        tableRow.addView(textView3)

        tableLayout.addView(tableRow)
    }
}