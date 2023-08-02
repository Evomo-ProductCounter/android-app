package com.evomo.productcounterapp.ui.main.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.evomo.productcounterapp.R

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