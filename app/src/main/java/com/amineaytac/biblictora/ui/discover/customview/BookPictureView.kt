package com.amineaytac.biblictora.ui.discover.customview

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.amineaytac.biblictora.R
import kotlin.math.max

class BookPictureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bitmap: Bitmap? = null

    private val matrix = Matrix()
    private val framePath = Path()
    private val frameStrokeWidth = 8.toDp
    private val viewRectF = RectF()

    private val imageRectF = RectF()
    private val imagePaint = Paint().apply {
        isAntiAlias = true
    }

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.STROKE
        strokeWidth = frameStrokeWidth.toFloat()
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewRectF.set(0f, 0f, w.toFloat(), h.toFloat())
        initImageMatrix()
        initFramePath()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(framePath)
        canvas.drawPath(framePath, fillPaint)
        bitmap?.let {
            canvas.drawBitmap(it, matrix, imagePaint)
        }
        canvas.drawPath(framePath, framePaint)
    }

    private fun initImageMatrix() {

        bitmap?.let { bitmap ->

            imageRectF.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

            val widthScale = viewRectF.width() / imageRectF.width()
            val heightScale = viewRectF.height() / imageRectF.height()
            val scaleFactor = max(widthScale, heightScale)
            val translateX = (viewRectF.width() - scaleFactor * imageRectF.width()) / 2f
            val translateY = (viewRectF.height() - scaleFactor * imageRectF.height()) / 2f

            matrix.setScale(scaleFactor, scaleFactor)
            matrix.postTranslate(translateX, translateY)
            invalidate()

        }
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

    fun setBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
        initImageMatrix()
        initFramePath()
        invalidate()
    }

    val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}
