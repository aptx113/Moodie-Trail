package com.danteyu.studio.moodietrail.statistic

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat


/**
 * Created by George Yu on 2020/2/25.
 */
class PieChartValueFormatter : ValueFormatter() {

    private val format = DecimalFormat("###,###,##0.0")

    override fun getFormattedValue(value: Float): String {
        return if (value != 0.0f) {
            format.format(value) + " %"
        } else {
            ""
        }
    }
}