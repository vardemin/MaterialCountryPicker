package com.github.vardemin.sectionedrecycleradapter

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ItemProvider {
    fun getSectionCount(): Int

    fun getItemCount(sectionIndex: Int): Int

    fun showHeadersForEmptySections(): Boolean

    fun showFooters(): Boolean
}