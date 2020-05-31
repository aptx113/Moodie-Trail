package com.danteyu.studio.moodietrail.component

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by George Yu on 2020/3/8.
 */
class DividerItemDecoration(divider: Drawable) : RecyclerView.ItemDecoration() {
    private val myDivider: Drawable = divider

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)

        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child: View = parent.getChildAt(i)
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val dividerTop: Int = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + myDivider.intrinsicHeight
            myDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            myDivider.draw(canvas)
        }
    }
}
