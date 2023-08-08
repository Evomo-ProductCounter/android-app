package com.evomo.productcounterapp.ui.main.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.evomo.productcounterapp.R

class BulletChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val barPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.green_700)
    }

    private val downtimePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.red)
    }

    private val minuteStampPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        textSize = 24f
    }

    // Draw border lines around the chart
    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }

    private var maxValue: Float = 60f // Set the maximum value to 60 (minutes)
    private var performanceRangeMin: Float = 0f
    private var performanceRangeMax: Float = 60f // Set the maximum performance range to 60 (minutes)

    private val durations = mutableListOf<Pair<Float, Float>>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate dimensions and positions
        val width = width.toFloat()
        val height = height.toFloat()

        // Draw green bar to fill the entire chart
        canvas.drawRect(0f, height / 4f, width, height * 3f / 4f, barPaint)

        // Draw bars with different colors based on durations
        if (durations.isNotEmpty()) {
            for (duration in durations) {
                val startMinute = duration.first
                val endMinute = duration.second

                val startXPos = width * startMinute / 60
                val endXPos = width * endMinute / 60

                canvas.drawRect(startXPos, height / 4f, endXPos, height * 3f / 4f, downtimePaint)
            }
        }

        // Calculate the width for each minute stamp to evenly distribute them across the chart
        val minuteWidth = width / 60

        // Draw minute stamps at intervals of 10 minutes
        for (minute in 0..60 step 10) {
            val position = minute * minuteWidth
//            canvas.drawLine(position, height / 4f, position, height * 3f / 4f, minuteStampPaint)

            // Draw text labels below the minute stamps
            val text = "$minute"
            val textWidth = minuteStampPaint.measureText(text)
            val textX = when (minute) {
                0 -> 0f // Left-align the 0 minute label
                60 -> width - textWidth // Right-align the 60 minute label
                else -> position - textWidth / 2 // Center other labels between the lines
            }
            val textY = height * 3f / 4f + 30 // Position the label below the line
            canvas.drawText(text, textX, textY, minuteStampPaint)
        }

        // Draw the top border line
        canvas.drawLine(0f, height / 4f, width, height / 4f, borderPaint)

        // Draw the bottom border line
        canvas.drawLine(0f, height * 3f / 4f, width, height * 3f / 4f, borderPaint)

        // Draw the left border line
        canvas.drawLine(0f, height / 4f, 0f, height * 3f / 4f, borderPaint)

        // Draw the right border line
        canvas.drawLine(width, height / 4f, width, height * 3f / 4f, borderPaint)
    }

    fun setMaxValue(maxValue: Float) {
        this.maxValue = maxValue
        invalidate()
    }

    fun setPerformanceRange(min: Float, max: Float) {
        this.performanceRangeMin = min
        this.performanceRangeMax = max
        invalidate()
    }

    fun setDurations(vararg durationPairs: Pair<Float, Float>) {
        durations.clear()
        for (duration in durationPairs) {
            durations.add(duration)
        }
        invalidate() // Trigger a redraw
    }

    fun clearDurations() {
        durations.clear()
        invalidate()
    }
}