package com.amineaytac.biblictora.ui.customview

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.amineaytac.biblictora.R

class BookPictureBackground @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val framePath = Path()
    private val frameStrokeWidth = 3.toDp.toFloat()
    private val viewRectF = RectF()

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.STROKE
        strokeWidth = frameStrokeWidth
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.toad)
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewRectF.set(0f, 0f, w.toFloat(), h.toFloat())
        initFramePath()
    }

    override fun onDraw(canvas: Canvas) {
        val saveLayer = canvas.saveLayer(viewRectF, null)
        canvas.drawRect(viewRectF, fillPaint)
        canvas.drawPath(framePath, clearPaint)
        canvas.drawPath(framePath, framePaint)
        canvas.restoreToCount(saveLayer)
    }

    private fun initFramePath() {
        val cornerRadius = 100f
        val inset = frameStrokeWidth / 2
        framePath.reset()

        framePath.moveTo(viewRectF.left + inset, viewRectF.top + inset)
        framePath.lineTo(viewRectF.right - inset, viewRectF.top + inset)
        framePath.lineTo(viewRectF.right - inset, viewRectF.bottom - inset)
        framePath.lineTo(viewRectF.left + cornerRadius, viewRectF.bottom - inset)

        framePath.arcTo(
            viewRectF.left + inset,
            viewRectF.bottom - 2 * cornerRadius + inset,
            2 * cornerRadius - inset,
            viewRectF.bottom - inset,
            90f,
            90f,
            false
        )

        framePath.lineTo(viewRectF.left + inset, viewRectF.top + cornerRadius)
        framePath.close()
        invalidate()
    }

    private val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}
