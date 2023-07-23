package com.evomo.productcounterapp.ui.main.customview

// BulletChartView.kt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.evomo.productcounterapp.R

//
//class BulletChartView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//    private var currentValue: Float = 0f // The current value to be displayed on the chart
//    private var targetValue: Float = 0f // The target value (desired value) for comparison
//    private var maxValue: Float = 100f // The maximum value for the chart
//    private var performanceRangeMin: Float = 0f // Minimum value of the performance range
//    private var performanceRangeMax: Float = 100f // Maximum value of the performance range
//
//    private val barPaint = Paint().apply {
//        color = Color.BLUE
//    }
//
//    private val targetPaint = Paint().apply {
//        color = Color.YELLOW
//    }
//
//    private val rangePaint = Paint().apply {
//        color = Color.RED
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // Calculate dimensions and positions
//        val width = width.toFloat()
//        val height = height.toFloat()
//        val barLength = width * currentValue / maxValue
//        val targetPosition = width * targetValue / maxValue
//        val rangeStartPosition = width * performanceRangeMin / maxValue
//        val rangeEndPosition = width * performanceRangeMax / maxValue
//
//        // Draw the performance range
//        canvas.drawRect(rangeStartPosition, height / 4f, rangeEndPosition, height * 3f / 4f, rangePaint)
//
//        // Draw the target line
//        canvas.drawLine(targetPosition, height / 4f, targetPosition, height * 3f / 4f, targetPaint)
//
//        // Draw the bar
//        canvas.drawRect(0f, height / 4f, barLength, height * 3f / 4f, barPaint)
//    }
//
//    fun setCurrentValue(currentValue: Float) {
//        this.currentValue = currentValue
//        invalidate() // Request a redraw
//    }
//
//    fun setTargetValue(targetValue: Float) {
//        this.targetValue = targetValue
//        invalidate()
//    }
//
//    fun setMaxValue(maxValue: Float) {
//        this.maxValue = maxValue
//        invalidate()
//    }
//
//    fun setPerformanceRange(min: Float, max: Float) {
//        this.performanceRangeMin = min
//        this.performanceRangeMax = max
//        invalidate()
//    }
//}

class BulletChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Define the bullet chart data properties
    var targetValue: Float = 0f
    var currentValue: Float = 0f
    var comparativeValue: Float = 0f

    // Define the paint objects for drawing
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.orange_500) // Set the target bar color
    }

    private val currentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.green_700) // Set the current value bar color
    }

    private val comparativePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.red) // Set the comparative value bar color
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK // Set the line color
        strokeWidth = dpToPx(1f) // Set the line thickness
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK // Set the text color
        textSize = dpToPx(14f) // Set the text size
    }

    // Helper method to convert dp to pixels
    private fun dpToPx(dp: Float): Float {
        val density = context.resources.displayMetrics.density
        return dp * density
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the target bar
        val targetBarWidth = width.toFloat()
        val targetBarHeight = height * 0.3f
        canvas.drawRect(0f, 0f, targetBarWidth, targetBarHeight, targetPaint)

        // Draw the current value bar
        val currentBarWidth = width * (currentValue / targetValue)
        canvas.drawRect(0f, 0f, currentBarWidth, targetBarHeight, currentPaint)

        // Draw the comparative value bar
        val comparativeBarWidth = width * (comparativeValue / targetValue)
        canvas.drawRect(0f, 0f, comparativeBarWidth, targetBarHeight, comparativePaint)

        // Draw the comparative value line
        val comparativeLineX = width * (comparativeValue / targetValue)
        canvas.drawLine(comparativeLineX, 0f, comparativeLineX, targetBarHeight, linePaint)

        // Draw minute stamps
        val intervalMinutes = 10
        val totalMinutes = 60
        for (i in 0..totalMinutes step intervalMinutes) {
            val minuteStampX = width * (i / totalMinutes.toFloat())

            // Center the minute stamps horizontally for 0 and 60, otherwise align to the left
            val text = i.toString()
            val textWidth = textPaint.measureText(text)
            val xPos = when (i) {
                0 -> 0f
                totalMinutes -> width - textWidth
                else -> minuteStampX - textWidth / 2f
            }

            // Adjust the vertical position of the minute stamps
            val yPos = targetBarHeight + dpToPx(20f)

            canvas.drawText(text, xPos, yPos, textPaint)
        }
    }
}