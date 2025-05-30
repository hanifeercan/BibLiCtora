package com.amineaytac.biblictora.ui.discover.customview

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.amineaytac.biblictora.R

class BookItemLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var whichItem: String? = null

    init {
        setWillNotDraw(false)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.BookItemLayout, 0, 0)
            whichItem = typedArray.getString(R.styleable.BookItemLayout_whichItem)
            typedArray.recycle()
        }
        if (whichItem != null) {
            if (whichItem == "item_reading_list") {
                changeFillPaint(true)
            }
        } else {
            changeFillPaint(false)
        }
    }

    private val framePath = Path()
    private val frameStrokeWidth = 8.toDp
    private val viewRectF = RectF()

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.STROKE
        strokeWidth = frameStrokeWidth.toFloat()
    }

    private lateinit var fillPaint: Paint

    private fun changeFillPaint(bool: Boolean) {
        if (bool) {
            fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ContextCompat.getColor(context, R.color.white)
                style = Paint.Style.FILL
            }
        } else {
            fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ContextCompat.getColor(context, R.color.toad)
                style = Paint.Style.FILL
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewRectF.set(0f, 0f, w.toFloat(), h.toFloat())
        initFramePath()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(framePath)
        canvas.drawPath(framePath, fillPaint)
        canvas.drawPath(framePath, framePaint)
    }

    private fun initFramePath() {
        val cornerRadius = 100f
        framePath.reset()

        framePath.moveTo(viewRectF.left, viewRectF.top)
        framePath.lineTo(viewRectF.right, viewRectF.top)
        framePath.lineTo(viewRectF.right, viewRectF.bottom)
        framePath.lineTo(viewRectF.left + cornerRadius, viewRectF.bottom)
        framePath.arcTo(
            viewRectF.left,
            viewRectF.bottom - 2 * cornerRadius,
            2 * cornerRadius,
            viewRectF.bottom,
            90f,
            90f,
            false
        )
        framePath.lineTo(viewRectF.left, viewRectF.top + cornerRadius)
        framePath.close()

        invalidate()
    }

    val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}