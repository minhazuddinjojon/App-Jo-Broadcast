package com.bracu.cse489.assignment2.ui.broadcast

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.bracu.cse489.assignment2.R

/**
 * A small circular gauge, drawn entirely with Canvas arcs (no external asset),
 * that animates smoothly from its current percentage to a new one and shifts
 * color (green -> amber -> red) to reflect the battery level. Used by
 * [BatteryBroadcastFragment] to visualize the live battery-changed broadcast.
 */
class BatteryRingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var animatedPercent = 0f
    private var hasReceivedValue = false
    private var animator: ValueAnimator? = null

    private val strokeWidthPx = resources.displayMetrics.density * 10f

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#33FFFFFF")
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.ROUND
        color = Color.WHITE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val arcBounds = RectF()

    fun setPercentage(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        val start = animatedPercent
        animator?.cancel()
        animator = ValueAnimator.ofFloat(start, clamped.toFloat()).apply {
            duration = 700
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                animatedPercent = it.animatedValue as Float
                invalidate()
            }
            start()
        }
        hasReceivedValue = true
        progressPaint.color = colorForPercent(clamped)
        contentDescription = "Battery level $clamped percent"
    }

    private fun colorForPercent(percent: Int): Int = when {
        percent <= 20 -> ContextCompat.getColor(context, R.color.battery_low)
        percent <= 50 -> ContextCompat.getColor(context, R.color.battery_mid)
        else -> ContextCompat.getColor(context, R.color.battery_good)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = strokeWidthPx / 2f + paddingLeft
        arcBounds.set(inset, inset, w - inset, h - inset)
        textPaint.textSize = w * 0.22f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(arcBounds, -90f, 360f, false, trackPaint)
        val sweep = 360f * (animatedPercent / 100f)
        if (sweep > 0f) {
            canvas.drawArc(arcBounds, -90f, sweep, false, progressPaint)
        }
        val label = if (hasReceivedValue) "${animatedPercent.toInt()}%" else "--"
        val yPos = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(label, width / 2f, yPos, textPaint)
    }
}
