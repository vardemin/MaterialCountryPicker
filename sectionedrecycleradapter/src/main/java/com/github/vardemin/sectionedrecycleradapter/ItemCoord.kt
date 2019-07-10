package com.github.vardemin.sectionedrecycleradapter

class ItemCoord(val section: Int, val relativePos: Int) {

    fun section(): Int {
        return section
    }

    fun relativePos(): Int {
        return relativePos
    }

    override fun equals(other: Any?): Boolean {
        return (other is ItemCoord
                && other.section() == section()
                && other.relativePos() == relativePos())
    }

    override fun toString(): String {
        return "$section:$relativePos"
    }
}