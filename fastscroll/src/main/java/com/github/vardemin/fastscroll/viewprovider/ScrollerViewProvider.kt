package com.github.vardemin.fastscroll.viewprovider

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.vardemin.fastscroll.FastScroller

abstract class ScrollerViewProvider {
    private var scroller: FastScroller? = null
    private var handleBehavior: ViewBehavior? = null
    private var bubbleBehavior: ViewBehavior? = null

    fun setFastScroller(scroller: FastScroller) {
        this.scroller = scroller
    }

    protected fun getContext(): Context? {
        return scroller?.context
    }

    protected fun getScroller(): FastScroller? {
        return scroller
    }

    /**
     * @param container The container [FastScroller] for the view to inflate properly.
     * @return A view which will be by the [FastScroller] used as a handle.
     */
    abstract fun provideHandleView(container: ViewGroup): View

    /**
     * @param container The container [FastScroller] for the view to inflate properly.
     * @return A view which will be by the [FastScroller] used as a bubble.
     */
    abstract fun provideBubbleView(container: ViewGroup): View

    /**
     * Bubble view has to provide a [TextView] that will show the index title.
     * @return A [TextView] that will hold the index title.
     */
    abstract fun provideBubbleTextView(): TextView

    /**
     * To offset the position of the bubble relative to the handle. E.g. in [DefaultScrollerViewProvider]
     * the sharp corner of the bubble is aligned with the center of the handle.
     * @return the position of the bubble in relation to the handle (according to the orientation).
     */
    abstract fun getBubbleOffset(): Int

    protected abstract fun provideHandleBehavior(): ViewBehavior?

    protected abstract fun provideBubbleBehavior(): ViewBehavior?

    protected fun getHandleBehavior(): ViewBehavior? {
        if (handleBehavior == null) handleBehavior = provideHandleBehavior()
        return handleBehavior
    }

    protected fun getBubbleBehavior(): ViewBehavior? {
        if (bubbleBehavior == null) bubbleBehavior = provideBubbleBehavior()
        return bubbleBehavior
    }

    fun onHandleGrabbed() {
        if (getHandleBehavior() != null) getHandleBehavior()!!.onHandleGrabbed()
        if (getBubbleBehavior() != null) getBubbleBehavior()!!.onHandleGrabbed()
    }

    fun onHandleReleased() {
        if (getHandleBehavior() != null) getHandleBehavior()!!.onHandleReleased()
        if (getBubbleBehavior() != null) getBubbleBehavior()!!.onHandleReleased()
    }

    fun onScrollStarted() {
        if (getHandleBehavior() != null) getHandleBehavior()!!.onScrollStarted()
        if (getBubbleBehavior() != null) getBubbleBehavior()!!.onScrollStarted()
    }

    fun onScrollFinished() {
        if (getHandleBehavior() != null) getHandleBehavior()!!.onScrollFinished()
        if (getBubbleBehavior() != null) getBubbleBehavior()!!.onScrollFinished()
    }
}