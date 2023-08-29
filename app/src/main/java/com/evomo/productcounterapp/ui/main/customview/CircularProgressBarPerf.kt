package com.evomo.productcounterapp.ui.main.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.evomo.productcounterapp.R

class CircularProgressBarPerf @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()
    private val circlePaint: Paint = Paint()
    private val rectF: RectF = RectF()
    private var progress: Int = 0
    private var max: Int = 100

    init {
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dpToPx(8f) // Adjust the stroke width as needed
        paint.color = ContextCompat.getColor(context, R.color.green_700) // Change the progress bar color

        circlePaint.isAntiAlias = true
        circlePaint.strokeCap = Paint.Cap.ROUND
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = dpToPx(8f)
        circlePaint.color = ContextCompat.getColor(context, R.color.grey_transparent)
    }

    private val textPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.BLACK // Set the color of the progress text
        textSize = dpToPx(20f) // Adjust the text size as needed
//        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val angle = 360f * progress / max
        canvas.drawArc(rectF, 90f, 360f, false, circlePaint)
        canvas.drawArc(rectF, 90f, angle, false, paint)

        // Draw the progress percentage text in the center
        val text = "${progress}%"
        val textWidth = textPaint.measureText(text)
        val textX = (width / 2f) - (textWidth / 2f)
        val textY = (height / 2f) + (textPaint.textSize / 2f)
        canvas.drawText(text, textX, textY, textPaint)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val size = if (w < h) w else h
        val centerX = (w / 2).toFloat()
        val centerY = (h / 2).toFloat()
        val radius = size / 2f - paint.strokeWidth / 2f

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
    }

    fun setProgress(progress: Int) {
        this.progress = progress

        if (progress <= max * 0.25) {
            paint.color = ContextCompat.getColor(context, R.color.red)
        }
        else if (progress <= max * 0.5) {
            paint.color = ContextCompat.getColor(context, R.color.orange_500)
        }
        else if (progress <= max * 0.75) {
            paint.color = ContextCompat.getColor(context, R.color.green_500)
        }
        else {
            paint.color = ContextCompat.getColor(context, R.color.green_700)
        }

        invalidate()
    }

    fun setMax(max: Int) {
        this.max = max
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}
