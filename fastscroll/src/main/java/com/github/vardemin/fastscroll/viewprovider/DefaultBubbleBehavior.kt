package com.github.vardemin.fastscroll.viewprovider

class DefaultBubbleBehavior(val animationManager: VisibilityAnimationManager) : ViewBehavior {

    override fun onHandleGrabbed() {
        animationManager.show()
    }

    override fun onHandleReleased() {
        animationManager.hide()
    }

    override fun onScrollStarted() {

    }

    override fun onScrollFinished() {

    }
}

/**
 * Created by Michal on 11/08/16.
 * Extending classes should use this interface to get notified about events that occur to the
 * fastscroller elements (handle and bubble) and react accordingly. See [DefaultBubbleBehavior]
 * for an example.
 */
interface ViewBehavior {
    fun onHandleGrabbed()
    fun onHandleReleased()
    fun onScrollStarted()
    fun onScrollFinished()
}