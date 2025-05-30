package com.amineaytac.biblictora.ui.discover.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.amineaytac.biblictora.R

class HeartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sweepAngle = 225f
    var isClicked = false

    private var onFavoriteClickListener: ((Boolean) -> Unit)? = null
    fun setOnFavoriteClickListener(listener: (Boolean) -> Unit) {
        onFavoriteClickListener = listener
    }

    private val heartPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val framePath = Path()
    private val viewRectF = RectF()
    private val startColor: Int
    private val endColor: Int

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.DrawHeart, 0, 0
        ).apply {
            try {
                startColor = getColor(
                    R.styleable.DrawHeart_startColor, ContextCompat.getColor(context, R.color.white)
                )
                endColor = getColor(
                    R.styleable.DrawHeart_endColor,
                    ContextCompat.getColor(context, R.color.black)
                )
            } finally {
                recycle()
            }
        }
        isClickable = true
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        isClicked = !isClicked
        heartPaint.color = if (isClicked) endColor else startColor
        onFavoriteClickListener?.invoke(isClicked)
        invalidate()
        return true
    }

    fun changeColor() {
        heartPaint.color = if (isClicked) endColor else startColor
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewRectF.set(0f, 0f, w.toFloat(), h.toFloat())
        initFramePath()
    }

    private fun initFramePath() {

        val centerX = viewRectF.width() / 2
        val centerY = viewRectF.height() / 2

        val heartWidth = viewRectF.width()
        val heartHeight = viewRectF.height()
        val bottomPointY = centerY + heartHeight / 2

        framePath.apply {
            reset()
            addArc(
                centerX - heartWidth / 2,
                centerY - heartHeight / 2,
                centerX,
                centerY,
                -225f,
                sweepAngle
            )

            arcTo(
                centerX,
                centerY - heartHeight / 2,
                centerX + heartWidth / 2,
                centerY,
                -180f,
                sweepAngle,
                false
            )

            lineTo(centerX, bottomPointY)
            close()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(framePath, heartPaint)
    }
}