package com.danteyu.studio.moodietrail.util

/**
 * Created by George Yu on 2020/2/23.
 */
class Range(private val fromInclusive: Boolean, val from: Float, val to: Float, private val toInclusive: Boolean) {

    infix operator fun contains(value: Float): Boolean {
        return when {
            fromInclusive && toInclusive -> value in from..to
            !fromInclusive && !toInclusive -> value > from && value < to
            !fromInclusive && toInclusive -> value > from && value <= to
            fromInclusive && !toInclusive -> value >= from && value < to
            else -> false
        }
    }
}