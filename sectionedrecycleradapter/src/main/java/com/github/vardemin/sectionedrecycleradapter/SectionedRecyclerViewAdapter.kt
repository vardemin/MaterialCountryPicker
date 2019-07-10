package com.github.vardemin.sectionedrecycleradapter

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

public abstract class SectionedRecyclerViewAdapter<VH: SectionViewHolder>: RecyclerView.Adapter<VH>(),ItemProvider {
    protected val VIEW_TYPE_FOOTER = -3
    protected val VIEW_TYPE_HEADER = -2
    protected val VIEW_TYPE_ITEM = -1
    private val TAG = "SectionedRVAdapter"

    private val positionManager: PositionManager = PositionManager()
    private var layoutManager: GridLayoutManager? = null
    private var showHeadersForEmptySections: Boolean = false
    private var showFooters: Boolean = false

    fun notifySectionChanged(@androidx.annotation.IntRange(from = 0, to = Integer.MAX_VALUE.toLong()) section: Int) {
        if (section < 0 || section > getSectionCount() - 1) {
            throw IllegalArgumentException(
                "Section $section is out of range of existing sections."
            )
        }
        val sectionHeaderIndex = positionManager.sectionHeaderIndex(section)
        if (sectionHeaderIndex == -1) {
            throw IllegalStateException("No header position mapped for section $section")
        }
        val sectionItemCount = getItemCount(section)
        if (sectionItemCount == 0) {
            Log.d(TAG, "There are no items in section $section to notify.")
            return
        }
        Log.d(
            TAG, "Invalidating $sectionItemCount items starting at index $sectionHeaderIndex"
        )
        notifyItemRangeChanged(sectionHeaderIndex, sectionItemCount)
    }

    fun expandSection(section: Int) {
        positionManager.expandSection(section)
        notifyDataSetChanged()
    }

    fun collapseSection(section: Int) {
        positionManager.collapseSection(section)
        notifyDataSetChanged()
    }

    fun expandAllSections() {
        if (!positionManager.hasInvalidated()) {
            positionManager.invalidate(this)
        }
        positionManager.expandAllSections()
        notifyDataSetChanged()
    }

    fun collapseAllSections() {
        if (!positionManager.hasInvalidated()) {
            positionManager.invalidate(this)
        }
        positionManager.collapseAllSections()
        notifyDataSetChanged()
    }

    fun toggleSectionExpanded(section: Int) {
        positionManager.toggleSectionExpanded(section)
        notifyDataSetChanged()
    }

    abstract override fun getSectionCount(): Int

    abstract override fun getItemCount(sectionIndex: Int): Int

    abstract fun onBindHeaderViewHolder(holder: VH, section: Int, expanded: Boolean)

    abstract fun onBindFooterViewHolder(holder: VH, section: Int)

    abstract fun onBindViewHolder(
        holder: VH, section: Int, relativePosition: Int, absolutePosition: Int
    )

    fun isHeader(position: Int): Boolean {
        return positionManager.isHeader(position)
    }

    fun isFooter(position: Int): Boolean {
        return positionManager.isFooter(position)
    }

    fun isSectionExpanded(section: Int): Boolean {
        return positionManager.isSectionExpanded(section)
    }

    fun getSectionHeaderIndex(section: Int): Int {
        return positionManager.sectionHeaderIndex(section)
    }

    fun getSectionFooterIndex(section: Int): Int {
        return positionManager.sectionFooterIndex(section)
    }

    /**
     * Toggle whether or not section headers are shown when a section has no items. Makes a call to
     * notifyDataSetChanged().
     */
    fun shouldShowHeadersForEmptySections(show: Boolean) {
        showHeadersForEmptySections = show
        notifyDataSetChanged()
    }

    /**
     * Toggle whether or not section footers are shown at the bottom of each section. Makes a call to
     * notifyDataSetChanged().
     */
    fun shouldShowFooters(show: Boolean) {
        showFooters = show
        notifyDataSetChanged()
    }

    fun setLayoutManager(lm: GridLayoutManager?) {
        layoutManager = lm
        if (lm == null) {
            return
        }
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (isHeader(position) || isFooter(position)) {
                    return layoutManager?.spanCount ?: -1
                }
                val sectionAndPos = getRelativePosition(position)
                val absPos = position - (sectionAndPos.section() + 1)
                return getRowSpan(
                    layoutManager?.spanCount ?: -1,
                    sectionAndPos.section(),
                    sectionAndPos.relativePos(),
                    absPos
                )
            }
        }
    }

    protected fun getRowSpan(
        fullSpanSize: Int, section: Int, relativePosition: Int, absolutePosition: Int
    ): Int {
        return 1
    }

    /** Converts an absolute position to a relative position and section.  */
    fun getRelativePosition(absolutePosition: Int): ItemCoord {
        return positionManager.relativePosition(absolutePosition)
    }

    /**
     * Converts a relative position (index inside of a section) to an absolute position (index out of
     * all items and headers).
     */
    fun getAbsolutePosition(sectionIndex: Int, relativeIndex: Int): Int {
        return positionManager.absolutePosition(sectionIndex, relativeIndex)
    }

    /**
     * Converts a relative position (index inside of a section) to an absolute position (index out of
     * all items and headers).
     */
    fun getAbsolutePosition(relativePosition: ItemCoord): Int {
        return positionManager.absolutePosition(relativePosition)
    }

    override fun getItemCount(): Int {
        return positionManager.invalidate(this)
    }

    override fun showHeadersForEmptySections(): Boolean {
        return showHeadersForEmptySections
    }

    override fun showFooters(): Boolean {
        return showFooters
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Deprecated("")
    override fun getItemId(position: Int): Long {
        return when {
            isHeader(position) -> {
                val pos = positionManager.sectionId(position)
                getHeaderId(pos)
            }
            isFooter(position) -> {
                val pos = positionManager.footerId(position)
                getFooterId(pos)
            }
            else -> {
                val sectionAndPos = getRelativePosition(position)
                getItemId(sectionAndPos.section(), sectionAndPos.relativePos())
            }
        }
    }

    fun getHeaderId(section: Int): Long {
        return super.getItemId(section)
    }

    fun getFooterId(section: Int): Long {
        return super.getItemId(section) + getItemCount(section)
    }

    fun getItemId(section: Int, position: Int): Long {
        return super.getItemId(position)
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Deprecated("")
    override fun getItemViewType(position: Int): Int {
        if (isHeader(position)) {
            return getHeaderViewType(positionManager.sectionId(position))
        } else if (isFooter(position)) {
            return getFooterViewType(positionManager.footerId(position))
        } else {
            val sectionAndPos = getRelativePosition(position)
            return getItemViewType(
                sectionAndPos.section(),
                // offset section view positions
                sectionAndPos.relativePos(),
                position - (sectionAndPos.section() + 1)
            )
        }
    }

    fun getHeaderViewType(section: Int): Int {

        return VIEW_TYPE_HEADER
    }

    fun getFooterViewType(section: Int): Int {

        return VIEW_TYPE_FOOTER
    }

    fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int {

        return VIEW_TYPE_ITEM
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Deprecated("")
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.positionDelegate = positionManager

        var layoutParams: StaggeredGridLayoutManager.LayoutParams? = null
        if (holder.itemView.layoutParams is GridLayoutManager.LayoutParams)
            layoutParams = StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        else if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        }

        if (isHeader(position)) {
            if (layoutParams != null) {
                layoutParams!!.setFullSpan(true)
            }
            val sectionIndex = positionManager.sectionId(position)
            onBindHeaderViewHolder(holder, sectionIndex, isSectionExpanded(sectionIndex))
        } else if (isFooter(position)) {
            if (layoutParams != null) {
                layoutParams!!.setFullSpan(true)
            }
            val sectionIndex = positionManager.footerId(position)
            onBindFooterViewHolder(holder, sectionIndex)
        } else {
            if (layoutParams != null) {
                layoutParams!!.setFullSpan(false)
            }
            val sectionAndPos = getRelativePosition(position)
            val absolutePosition = getAbsolutePosition(sectionAndPos)
            onBindViewHolder(
                holder,
                sectionAndPos.section(),
                // offset section view positions
                sectionAndPos.relativePos(),
                absolutePosition
            )
        }

        if (layoutParams != null) {
            holder.itemView.layoutParams = layoutParams
        }
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Deprecated("deprecated")
    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }
}