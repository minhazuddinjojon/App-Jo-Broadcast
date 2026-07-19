package com.bracu.cse489.assignment2.ui.imagescale

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs

/**
 * An [AppCompatImageView] that supports:
 *  - Pinch-to-zoom (1x - 5x), pivoting around the pinch focus point.
 *  - Single-finger drag-to-pan once zoomed in, clamped so the image can never be
 *    dragged past its own edge (the math keeps the scaled content fully covering
 *    the view at all times).
 *  - Double-tap to smoothly reset back to 1x / centered.
 *
 * Zoom/pan are applied through plain View properties (scaleX/scaleY/translationX/
 * translationY) rather than a Matrix. That keeps the math simple and independent of
 * the loaded drawable's own intrinsic size, so it's safe to use even while an image
 * is still loading asynchronously (e.g. via Picasso). The parent should keep normal
 * (default) clipChildren behaviour so the zoomed/panned content is clipped back to
 * this view's own bounds - i.e. it never bleeds outside its designated viewport.
 */
class PinchZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var scaleFactor = 1f
    private val minScale = 1f
    private val maxScale = 5f

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var isDragging = false

    /** Notified whenever the current zoom level changes, e.g. to drive a "180%" badge. */
    var onScaleChanged: ((Float) -> Unit)? = null

    private val scaleGestureDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val previousScale = scaleFactor
                scaleFactor = (scaleFactor * detector.scaleFactor).coerceIn(minScale, maxScale)
                if (scaleFactor != previousScale) {
                    // Pivot scaling around the pinch center
                    pivotX = detector.focusX
                    pivotY = detector.focusY
                    
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    clampTranslation()
                    onScaleChanged?.invoke(scaleFactor)
                }
                return true
            }
        }
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onDoubleTap(e: MotionEvent): Boolean {
                resetZoom()
                return true
            }
        }
    )

    init {
        isClickable = true
    }

    fun resetZoom() {
        scaleFactor = 1f
        animate().scaleX(1f).scaleY(1f).translationX(0f).translationY(0f).setDuration(200).start()
        onScaleChanged?.invoke(1f)
    }

    private fun clampTranslation() {
        val scaledWidth = width * scaleFactor
        val scaledHeight = height * scaleFactor
        val maxTranslationX = (scaledWidth - width) / 2f
        val maxTranslationY = (scaledHeight - height) / 2f
        translationX = translationX.coerceIn(-maxTranslationX, maxTranslationX)
        translationY = translationY.coerceIn(-maxTranslationY, maxTranslationY)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                activePointerId = event.getPointerId(0)
                isDragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!scaleGestureDetector.isInProgress && scaleFactor > 1f) {
                    val pointerIndex = event.findPointerIndex(activePointerId)
                    if (pointerIndex != -1) {
                        val x = event.getX(pointerIndex)
                        val y = event.getY(pointerIndex)
                        val dx = x - lastTouchX
                        val dy = y - lastTouchY
                        if (isDragging || abs(dx) > 4 || abs(dy) > 4) {
                            isDragging = true
                            translationX += dx
                            translationY += dy
                            clampTranslation()
                        }
                        lastTouchX = x
                        lastTouchY = y
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    val newIndex = if (pointerIndex == 0) 1 else 0
                    if (newIndex < event.pointerCount) {
                        lastTouchX = event.getX(newIndex)
                        lastTouchY = event.getY(newIndex)
                        activePointerId = event.getPointerId(newIndex)
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
                isDragging = false
            }
        }
        return true
    }
}
