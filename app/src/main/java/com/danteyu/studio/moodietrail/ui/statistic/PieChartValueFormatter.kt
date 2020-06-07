package com.danteyu.studio.moodietrail.ui.statistic

import com.github.mikephil.charting.formatter.ValueFormatter
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
