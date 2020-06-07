package com.danteyu.studio.moodietrail.ui.psytestresult

import android.util.SparseArray
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * Created by George Yu on 2020/3/5.
 */
class BarChartXValueFormatter(private val xValsDateLabel: SparseArray<String>) : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
        return if (value.toInt() >= 0) {
            xValsDateLabel[value.toInt()]
        } else {
            ("").toString()
        }
    }
}
