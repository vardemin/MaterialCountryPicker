package com.github.vardemin.fastscroll.viewprovider

import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.vardemin.fastscroll.R
import com.github.vardemin.fastscroll.setBackground

public class DefaultScrollerViewProvider : ScrollerViewProvider() {

    private var bubble: View? = null
    private var handle: View? = null

    override fun provideHandleView(container: ViewGroup): View {
        handle = View(getContext())

        val verticalInset =
            if (getScroller()!!.isVertical()) 0 else getContext()!!.resources.getDimensionPixelSize(R.dimen.fastscroll__handle_inset)
        val horizontalInset =
            if (!getScroller()!!.isVertical()) 0 else getContext()!!.resources.getDimensionPixelSize(R.dimen.fastscroll__handle_inset)
        val handleBg = InsetDrawable(
            ContextCompat.getDrawable(getContext()!!, R.drawable.fastscroll__default_handle),
            horizontalInset,
            verticalInset,
            horizontalInset,
            verticalInset
        )
        setBackground(handle!!, handleBg)

        val handleWidth = getContext()!!.resources
            .getDimensionPixelSize(if (getScroller()!!.isVertical()) R.dimen.fastscroll__handle_clickable_width else R.dimen.fastscroll__handle_height)
        val handleHeight = getContext()!!.resources
            .getDimensionPixelSize(if (getScroller()!!.isVertical()) R.dimen.fastscroll__handle_height else R.dimen.fastscroll__handle_clickable_width)
        val params = ViewGroup.LayoutParams(handleWidth, handleHeight)
        handle!!.layoutParams = params

        return handle!!
    }

    override fun provideBubbleView(container: ViewGroup): View {
        bubble = LayoutInflater.from(getContext()).inflate(R.layout.fastscroll__default_bubble, container, false)
        return bubble as View
    }

    override fun provideBubbleTextView(): TextView {
        return bubble as TextView
    }

    override fun getBubbleOffset(): Int {
        return (if (getScroller()!!.isVertical()) handle!!.height.toFloat() / 2f - bubble!!.height else handle!!.width.toFloat() / 2f - bubble!!.width).toInt()
    }

    protected override fun provideHandleBehavior(): ViewBehavior? {
        return null
    }

    protected override fun provideBubbleBehavior(): ViewBehavior {
        return DefaultBubbleBehavior(Builder(bubble!!).withPivotX(1f).withPivotY(1f).build())
    }
}