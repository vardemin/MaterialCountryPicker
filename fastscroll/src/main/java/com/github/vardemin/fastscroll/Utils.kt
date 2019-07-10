package com.github.vardemin.fastscroll

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View

fun getViewRawY(view: View): Float {
    val location = IntArray(2)
    location[0] = 0
    location[1] = view.y.toInt()
    (view.parent as View).getLocationInWindow(location)
    return location[1].toFloat()
}

fun getViewRawX(view: View): Float {
    val location = IntArray(2)
    location[0] = view.x.toInt()
    location[1] = 0
    (view.parent as View).getLocationInWindow(location)
    return location[0].toFloat()
}

fun getValueInRange(min: Float, max: Float, value: Float): Float {
    val minimum = kotlin.math.max(min, value)
    return kotlin.math.min(minimum, max)
}

fun setBackground(view: View, drawable: Drawable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        view.background = drawable
    } else {
        view.setBackgroundDrawable(drawable)
    }
}