package com.github.vardemin.sectionedrecycleradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var positionDelegate: PositionDelegate? = null

    protected fun getRelativePosition(): ItemCoord? {
        return positionDelegate?.relativePosition(adapterPosition)
    }

    protected fun isHeader(): Boolean {
        return positionDelegate?.isHeader(adapterPosition) == true
    }

    protected fun isFooter(): Boolean {
        return positionDelegate?.isFooter(adapterPosition) == true
    }
}

interface PositionDelegate {
    fun relativePosition(absolutePosition: Int): ItemCoord

    fun isHeader(absolutePosition: Int): Boolean

    fun isFooter(absolutePosition: Int): Boolean
}