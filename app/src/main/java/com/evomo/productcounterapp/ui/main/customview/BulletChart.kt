package com.evomo.productcounterapp.ui.main.customview

// BulletChartView.kt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BulletChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var currentValue: Float = 0f // The current value to be displayed on the chart
    private var targetValue: Float = 0f // The target value (desired value) for comparison
    private var maxValue: Float = 100f // The maximum value for the chart
    private var performanceRangeMin: Float = 0f // Minimum value of the performance range
    private var performanceRangeMax: Float = 100f // Maximum value of the performance range

    private val barPaint = Paint().apply {
        color = Color.BLUE
    }

    private val targetPaint = Paint().apply {
        color = Color.GREEN
    }

    private val rangePaint = Paint().apply {
        color = Color.LTGRAY
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate dimensions and positions
        val width = width.toFloat()
        val height = height.toFloat()
        val barLength = width * currentValue / maxValue
        val targetPosition = width * targetValue / maxValue
        val rangeStartPosition = width * performanceRangeMin / maxValue
        val rangeEndPosition = width * performanceRangeMax / maxValue

        // Draw the performance range
        canvas.drawRect(rangeStartPosition, height / 4f, rangeEndPosition, height * 3f / 4f, rangePaint)

        // Draw the target line
        canvas.drawLine(targetPosition, height / 4f, targetPosition, height * 3f / 4f, targetPaint)

        // Draw the bar
        canvas.drawRect(0f, height / 4f, barLength, height * 3f / 4f, barPaint)
    }

    fun setCurrentValue(currentValue: Float) {
        this.currentValue = currentValue
        invalidate() // Request a redraw
    }

    fun setTargetValue(targetValue: Float) {
        this.targetValue = targetValue
        invalidate()
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
}
