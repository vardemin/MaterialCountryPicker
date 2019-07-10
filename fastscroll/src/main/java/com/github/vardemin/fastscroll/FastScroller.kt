package com.github.vardemin.fastscroll

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.vardemin.fastscroll.viewprovider.DefaultScrollerViewProvider
import com.github.vardemin.fastscroll.viewprovider.ScrollerViewProvider

class FastScroller @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {
    private val STYLE_NONE = -1
    private val scrollListener = RecyclerViewScrollListener(this)
    private var recyclerView: RecyclerView? = null

    private var bubble: View? = null
    private var handle: View? = null
    private var bubbleTextView: TextView? = null

    private var bubbleOffset: Int = 0
    private var handleColor: Int = 0
    private var bubbleColor: Int = 0
    private var bubbleTextAppearance: Int = 0
    private var scrollerOrientation: Int = 0

    //TODO the name should be fixed, also check if there is a better way of handling the visibility, because this is somewhat convoluted
    private var maxVisibility: Int = 0

    private var manuallyChangingPosition: Boolean = false

    private var viewProvider: ScrollerViewProvider? = null
    private var titleProvider: SectionTitleProvider? = null

    init {
        clipChildren = false
        val style =
            context.obtainStyledAttributes(attrs, R.styleable.fastscroll__fastScroller, R.attr.fastscroll__style, 0)
        try {
            bubbleColor = style.getColor(R.styleable.fastscroll__fastScroller_fastscroll__bubbleColor, STYLE_NONE)
            handleColor = style.getColor(R.styleable.fastscroll__fastScroller_fastscroll__handleColor, STYLE_NONE)
            bubbleTextAppearance =
                style.getResourceId(R.styleable.fastscroll__fastScroller_fastscroll__bubbleTextAppearance, STYLE_NONE)
        } finally {
            style.recycle()
        }
        maxVisibility = visibility
        setViewProvider(DefaultScrollerViewProvider())
    }

    /**
     * Enables custom layout for [FastScroller].
     * @param viewProvider A [ScrollerViewProvider] for the [FastScroller] to use when building layout.
     */
    fun setViewProvider(viewProvider: ScrollerViewProvider) {
        removeAllViews()
        this.viewProvider = viewProvider
        viewProvider.setFastScroller(this)
        bubble = viewProvider.provideBubbleView(this)
        handle = viewProvider.provideHandleView(this)
        bubbleTextView = viewProvider.provideBubbleTextView()
        addView(bubble)
        addView(handle)
    }

    /**
     * Attach the [FastScroller] to [RecyclerView]. Should be used after the adapter is set
     * to the [RecyclerView]. If the adapter implements SectionTitleProvider, the FastScroller
     * will show a bubble with title.
     * @param recyclerView A [RecyclerView] to attach the [FastScroller] to.
     */
    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        if (recyclerView.adapter is SectionTitleProvider) titleProvider = recyclerView.adapter as SectionTitleProvider
        recyclerView.addOnScrollListener(scrollListener)
        invalidateVisibility()
        recyclerView.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                invalidateVisibility()
            }

            override fun onChildViewRemoved(parent: View, child: View) {
                invalidateVisibility()
            }
        })
    }

    /**
     * Set the orientation of the [FastScroller]. The orientation of the [FastScroller]
     * should generally match the orientation of connected  [RecyclerView] for good UX but it's not enforced.
     * Note: This method is overridden from [LinearLayout.setOrientation] but for [FastScroller]
     * it has a totally different meaning.
     * @param orientation of the [FastScroller]. [.VERTICAL] or [.HORIZONTAL]
     */
    override fun setOrientation(orientation: Int) {
        scrollerOrientation = orientation
        //switching orientation, because orientation in linear layout
        //is something different than orientation of fast scroller
        super.setOrientation(if (orientation == LinearLayout.HORIZONTAL) VERTICAL else LinearLayout.HORIZONTAL)
    }

    /**
     * Set the background color of the bubble.
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    fun setBubbleColor(color: Int) {
        bubbleColor = color
        invalidate()
    }

    /**
     * Set the background color of the handle.
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    fun setHandleColor(color: Int) {
        handleColor = color
        invalidate()
    }

    /**
     * Sets the text appearance of the bubble.
     * @param textAppearanceResourceId The id of the resource to be used as text appearance of the bubble.
     */
    fun setBubbleTextAppearance(textAppearanceResourceId: Int) {
        bubbleTextAppearance = textAppearanceResourceId
        invalidate()
    }

    /**
     * Add a [com.futuremind.recyclerviewfastscroll.RecyclerViewScrollListener.ScrollerListener]
     * to be notified of user scrolling
     * @param listener
     */
    fun addScrollerListener(listener: ScrollerListener) {
        scrollListener.addScrollerListener(listener)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        initHandleMovement()
        bubbleOffset = viewProvider!!.getBubbleOffset()

        applyStyling() //TODO this doesn't belong here, even if it works

        //sometimes recycler starts with a defined scroll (e.g. when coming from saved state)
        scrollListener.updateHandlePosition(recyclerView!!)

    }

    private fun applyStyling() {
        if (bubbleColor != STYLE_NONE) setBackgroundTint(bubbleTextView!!, bubbleColor)
        if (handleColor != STYLE_NONE) setBackgroundTint(handle!!, handleColor)
        if (bubbleTextAppearance != STYLE_NONE) TextViewCompat.setTextAppearance(bubbleTextView!!, bubbleTextAppearance)
    }

    private fun setBackgroundTint(view: View, color: Int) {
        val background = DrawableCompat.wrap(view.background) ?: return
        DrawableCompat.setTint(background.mutate(), color)
        setBackground(view, background)
    }

    private fun initHandleMovement() {
        handle!!.setOnTouchListener(OnTouchListener { _, event ->
            requestDisallowInterceptTouchEvent(true)
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                if (titleProvider != null && event.action == MotionEvent.ACTION_DOWN) viewProvider!!.onHandleGrabbed()
                manuallyChangingPosition = true
                val relativePos = getRelativeTouchPosition(event)
                setScrollerPosition(relativePos)
                setRecyclerViewPosition(relativePos)
                return@OnTouchListener true
            } else if (event.action == MotionEvent.ACTION_UP) {
                manuallyChangingPosition = false
                if (titleProvider != null) viewProvider!!.onHandleReleased()
                return@OnTouchListener true
            }
            false
        })
    }

    private fun getRelativeTouchPosition(event: MotionEvent): Float {
        return if (isVertical()) {
            val yInParent = event.rawY - getViewRawY(handle!!)
            yInParent / (height - handle!!.height)
        } else {
            val xInParent = event.rawX - getViewRawX(handle!!)
            xInParent / (width - handle!!.width)
        }
    }

    override fun setVisibility(visibility: Int) {
        maxVisibility = visibility
        invalidateVisibility()
    }

    private fun invalidateVisibility() {
        if (recyclerView!!.adapter == null ||
            recyclerView!!.adapter!!.itemCount === 0 ||
            recyclerView!!.getChildAt(0) == null ||
            isRecyclerViewNotScrollable() ||
            maxVisibility != View.VISIBLE
        ) {
            super.setVisibility(View.INVISIBLE)
        } else {
            super.setVisibility(View.VISIBLE)
        }
    }

    private fun isRecyclerViewNotScrollable(): Boolean {
        return if (isVertical()) {
            recyclerView!!.getChildAt(0).height * recyclerView!!.adapter!!.itemCount <= recyclerView!!.height
        } else {
            recyclerView!!.getChildAt(0).width * recyclerView!!.adapter!!.itemCount <= recyclerView!!.width
        }
    }

    private fun setRecyclerViewPosition(relativePos: Float) {
        if (recyclerView == null) return
        val itemCount = recyclerView!!.adapter!!.itemCount
        val targetPos = getValueInRange(0f, (itemCount - 1).toFloat(),
            (relativePos * itemCount.toFloat())
        ).toInt()
        recyclerView!!.scrollToPosition(targetPos)
        if (titleProvider != null && bubbleTextView != null) bubbleTextView?.text = titleProvider?.getSectionTitle(
            targetPos
        )
    }

    internal fun setScrollerPosition(relativePos: Float) {
        if (isVertical()) {
            bubble?.y = getValueInRange(
                0f,
                (height - bubble!!.height).toFloat(),
                relativePos * (height - handle!!.height) + bubbleOffset
            )
            handle?.y = getValueInRange(
                0f,
                (height - handle!!.height).toFloat(),
                relativePos * (height - handle!!.height)
            )
        } else {
            bubble?.x = getValueInRange(
                0f,
                (width - bubble!!.width).toFloat(),
                relativePos * (width - handle!!.width) + bubbleOffset
            )
            handle?.x = getValueInRange(
                0f,
                (width - handle!!.width).toFloat(),
                relativePos * (width - handle!!.width)
            )
        }
    }

    fun isVertical(): Boolean {
        return scrollerOrientation == VERTICAL
    }

    internal fun shouldUpdateHandlePosition(): Boolean {
        return handle != null && !manuallyChangingPosition && recyclerView!!.getChildCount() > 0
    }

    internal fun getViewProvider(): ScrollerViewProvider {
        return viewProvider!!
    }
}