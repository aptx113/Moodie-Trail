package com.danteyu.studio.moodietrail.statistic

import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * Created by George Yu on 2020/2/24.
 */
class LineChartXValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()+"日"
    }
}
