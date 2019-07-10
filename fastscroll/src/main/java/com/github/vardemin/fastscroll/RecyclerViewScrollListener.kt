package com.github.vardemin.fastscroll

import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class RecyclerViewScrollListener(val scroller: FastScroller): RecyclerView.OnScrollListener() {
    internal var listeners: MutableList<ScrollerListener> = ArrayList<ScrollerListener>()
    internal var oldScrollState = RecyclerView.SCROLL_STATE_IDLE

    fun addScrollerListener(listener: ScrollerListener) {
        listeners.add(listener)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newScrollState: Int) {
        super.onScrollStateChanged(recyclerView, newScrollState)
        if (newScrollState == RecyclerView.SCROLL_STATE_IDLE && oldScrollState != RecyclerView.SCROLL_STATE_IDLE) {
            scroller.getViewProvider().onScrollFinished()
        } else if (newScrollState != RecyclerView.SCROLL_STATE_IDLE && oldScrollState == RecyclerView.SCROLL_STATE_IDLE) {
            scroller.getViewProvider().onScrollStarted()
        }
        oldScrollState = newScrollState
    }

    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
        if (scroller.shouldUpdateHandlePosition()) {
            updateHandlePosition(rv)
        }
    }

    internal fun updateHandlePosition(rv: RecyclerView) {
        val relativePos: Float
        if (scroller.isVertical()) {
            val offset = rv.computeVerticalScrollOffset()
            val extent = rv.computeVerticalScrollExtent()
            val range = rv.computeVerticalScrollRange()
            relativePos = offset / (range - extent).toFloat()
        } else {
            val offset = rv.computeHorizontalScrollOffset()
            val extent = rv.computeHorizontalScrollExtent()
            val range = rv.computeHorizontalScrollRange()
            relativePos = offset / (range - extent).toFloat()
        }
        scroller.setScrollerPosition(relativePos)
        notifyListeners(relativePos)
    }

    fun notifyListeners(relativePos: Float) {
        for (listener in listeners) listener.onScroll(relativePos)
    }

}

interface ScrollerListener {
    fun onScroll(relativePos: Float)
}