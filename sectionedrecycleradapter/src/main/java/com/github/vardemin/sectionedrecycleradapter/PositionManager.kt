package com.github.vardemin.sectionedrecycleradapter

import androidx.collection.ArrayMap

class PositionManager: PositionDelegate {

    private val headerLocationMap: ArrayMap<Int, Int> = ArrayMap()
    private val footerLocationMap: ArrayMap<Int, Int> = ArrayMap()
    private val collapsedSectionMap: ArrayMap<Int, Boolean> = ArrayMap()

    private var itemProvider: ItemProvider? = null
    private var hasInvalidated: Boolean = false

    fun hasInvalidated(): Boolean {
        return hasInvalidated
    }

    fun invalidate(itemProvider: ItemProvider): Int {
        this.hasInvalidated = true
        this.itemProvider = itemProvider
        var count = 0
        headerLocationMap.clear()
        footerLocationMap.clear()
        for (s in 0 until itemProvider.getSectionCount()) {
            val itemCount = itemProvider.getItemCount(s)
            if (collapsedSectionMap[s] != null) {
                headerLocationMap[count] = s
                count += 1
                continue
            }
            if (itemProvider.showHeadersForEmptySections() || itemCount > 0) {
                headerLocationMap[count] = s
                count += itemCount + 1
                if (itemProvider.showFooters()) {
                    footerLocationMap[count] = s
                    count += 1
                }
            }
        }
        return count
    }

    override fun isHeader(absolutePosition: Int): Boolean {
        return headerLocationMap[absolutePosition] != null
    }

    override fun isFooter(absolutePosition: Int): Boolean {
        return footerLocationMap[absolutePosition] != null
    }

    fun sectionId(absolutePosition: Int): Int {
        return headerLocationMap[absolutePosition] ?: return -1
    }

    fun footerId(absolutePosition: Int): Int {
        return footerLocationMap[absolutePosition] ?: return -1
    }

    fun sectionHeaderIndex(section: Int): Int {
        for (key in headerLocationMap.keys) {
            if (headerLocationMap[key] == section) {
                return key!!
            }
        }
        return -1
    }

    fun sectionFooterIndex(section: Int): Int {
        for (key in footerLocationMap.keys) {
            if (footerLocationMap[key] == section) {
                return key!!
            }
        }
        return -1
    }

    override fun relativePosition(absolutePosition: Int): ItemCoord {
        val absHeaderLoc = headerLocationMap[absolutePosition]
        if (absHeaderLoc != null) {
            return ItemCoord(absHeaderLoc, -1)
        }
        var lastSectionIndex: Int? = -1
        for (sectionIndex in headerLocationMap.keys) {
            if (absolutePosition > sectionIndex) {
                lastSectionIndex = sectionIndex
            } else {
                break
            }
        }
        return ItemCoord(
            headerLocationMap[lastSectionIndex] ?: -1, absolutePosition - lastSectionIndex!! - 1
        )
    }

    /**
     * Converts a relative position (index inside of a section) to an absolute position (index out of
     * all items and headers).
     */
    fun absolutePosition(sectionIndex: Int, relativeIndex: Int): Int {
        if (sectionIndex < 0 || sectionIndex > (itemProvider?.getSectionCount() ?: 0) - 1) {
            return -1
        }
        val sectionHeaderIndex = sectionHeaderIndex(sectionIndex)
        return if (relativeIndex > (itemProvider?.getItemCount(sectionIndex)?: 0) - 1) {
            -1
        } else sectionHeaderIndex + (relativeIndex + 1)
    }

    /**
     * Converts a relative position (index inside of a section) to an absolute position (index out of
     * all items and headers).
     */
    fun absolutePosition(relativePosition: ItemCoord): Int {
        return absolutePosition(relativePosition.section(), relativePosition.relativePos())
    }

    fun expandSection(section: Int) {
        if (section < 0 || section > (itemProvider?.getSectionCount() ?: 0) - 1) {
            throw IllegalArgumentException("Section $section is out of bounds.")
        }
        collapsedSectionMap.remove(section)
    }

    fun collapseSection(section: Int) {
        if (section < 0 || section > (itemProvider?.getSectionCount() ?: 0) - 1) {
            throw IllegalArgumentException("Section $section is out of bounds.")
        }
        collapsedSectionMap[section] = true
    }

    fun toggleSectionExpanded(section: Int) {
        if (collapsedSectionMap[section] != null) {
            expandSection(section)
        } else {
            collapseSection(section)
        }
    }

    fun expandAllSections() {
        for (i in 0 until (itemProvider?.getSectionCount() ?: 0)) {
            expandSection(i)
        }
    }

    fun collapseAllSections() {
        for (i in 0 until (itemProvider?.getSectionCount() ?: 0)) {
            collapseSection(i)
        }
    }

    fun isSectionExpanded(section: Int): Boolean {
        if (section < 0 || section > (itemProvider?.getSectionCount() ?: 0) - 1) {
            throw IllegalArgumentException("Section $section is out of bounds.")
        }
        return collapsedSectionMap[section] == null
    }
}