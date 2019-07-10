package com.github.vardemin.fastscroll.viewprovider

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.view.View
import androidx.annotation.AnimatorRes
import com.github.vardemin.fastscroll.R

class VisibilityAnimationManager(val view: View,
                                @AnimatorRes showAnimator: Int,
                                @AnimatorRes hideAnimator: Int,
                                val pivotXRelative: Float,
                                val pivotYRelative: Float,
                                hideDelay: Int) {


    private val showAnimator: AnimatorSet
    private val hideAnimator: AnimatorSet

    init {
        this.hideAnimator = AnimatorInflater.loadAnimator(view.context, hideAnimator) as AnimatorSet
        this.hideAnimator.startDelay = hideDelay.toLong()
        this.hideAnimator.setTarget(view)
        this.showAnimator = AnimatorInflater.loadAnimator(view.context, showAnimator) as AnimatorSet
        this.showAnimator.setTarget(view)
        this.hideAnimator.addListener(object : AnimatorListenerAdapter() {

            //because onAnimationEnd() goes off even for canceled animations
            var wasCanceled: Boolean = false

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!wasCanceled) view.visibility = View.INVISIBLE
                wasCanceled = false
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                wasCanceled = true
            }
        })
        updatePivot()
    }


    fun show() {
        hideAnimator.cancel()
        if (view.visibility == View.INVISIBLE) {
            view.visibility = View.VISIBLE
            updatePivot()
            showAnimator.start()
        }
    }

    fun hide() {
        updatePivot()
        hideAnimator.start()
    }

    protected fun updatePivot() {
        view.pivotX = pivotXRelative * view.measuredWidth
        view.pivotY = pivotYRelative * view.measuredHeight
    }

}

abstract class AbsBuilder<out VisibilityAnimationManager>(protected val view: View) {
    protected var showAnimatorResource = R.animator.fastscroll__default_show
    protected var hideAnimatorResource = R.animator.fastscroll__default_hide
    protected var hideDelay = 1000
    protected var pivotX = 0.5f
    protected var pivotY = 0.5f

    fun withShowAnimator(@AnimatorRes showAnimatorResource: Int): AbsBuilder<VisibilityAnimationManager> {
        this.showAnimatorResource = showAnimatorResource
        return this
    }

    fun withHideAnimator(@AnimatorRes hideAnimatorResource: Int): AbsBuilder<VisibilityAnimationManager> {
        this.hideAnimatorResource = hideAnimatorResource
        return this
    }

    fun withHideDelay(hideDelay: Int): AbsBuilder<VisibilityAnimationManager> {
        this.hideDelay = hideDelay
        return this
    }

    fun withPivotX(pivotX: Float): AbsBuilder<VisibilityAnimationManager> {
        this.pivotX = pivotX
        return this
    }

    fun withPivotY(pivotY: Float): AbsBuilder<VisibilityAnimationManager> {
        this.pivotY = pivotY
        return this
    }

    abstract fun build(): VisibilityAnimationManager
}

class Builder(view: View) : AbsBuilder<VisibilityAnimationManager>(view) {

    override fun build(): VisibilityAnimationManager {
        return VisibilityAnimationManager(view, showAnimatorResource, hideAnimatorResource, pivotX, pivotY, hideDelay)
    }

}