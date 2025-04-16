package com.amineaytac.biblictora.ui.mybooks

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amineaytac.biblictora.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeGesture(context: Context) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT
) {
    private val deleteColor = ContextCompat.getColor(context, R.color.medium_grey_green)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val radius = 77f

        if (dX < 0) {
            val bgPaint = Paint().apply {
                color = deleteColor
                isAntiAlias = true
            }

            val rectF = RectF(
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )

            c.drawRoundRect(rectF, radius, radius, bgPaint)

            val strokePaint = Paint().apply {
                color = deleteColor
                style = Paint.Style.STROKE
                strokeWidth = strokeWidth
                isAntiAlias = true
            }
            c.drawRoundRect(rectF, radius, radius, strokePaint)

            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                .create()
                .decorate()
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    abstract override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
}