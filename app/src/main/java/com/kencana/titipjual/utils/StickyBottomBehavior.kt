package com.kencana.titipjual.utils

import android.view.View
import android.view.View.SCROLL_AXIS_VERTICAL
import android.view.ViewGroup.MarginLayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout

class StickyBottomBehavior(
    private val mAnchorId: Int,
    private val mNotStickyMargin: Int,
    private val actionBarHeight: Int = 0,
    private val statusBarHeight: Int = 0
) :
    CoordinatorLayout.Behavior<View?>() {
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return (axes == SCROLL_AXIS_VERTICAL)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        val anchor: View = coordinatorLayout.findViewById(mAnchorId)
        val anchorLocation = IntArray(2)
        anchor.getLocationInWindow(anchorLocation)
        val coordBottom = coordinatorLayout.bottom

        // vertical position, cannot scroll over the bottom of the coordinator layout
        child.setY(
            Math.min(
                anchorLocation[1].minus(actionBarHeight + statusBarHeight),
                coordBottom - child.getHeight()
            ).toFloat()
        )

        // Margins depend on the distance to the bottom
        val diff = Math.max(coordBottom - anchorLocation[1] - child.getHeight(), 0)
        val layoutParams = child.getLayoutParams() as MarginLayoutParams
        layoutParams.leftMargin = Math.min(diff, mNotStickyMargin)
        layoutParams.rightMargin = Math.min(diff, mNotStickyMargin)
        child.setLayoutParams(layoutParams)
    }
}
