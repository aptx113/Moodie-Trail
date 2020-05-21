package com.danteyu.studio.moodietrail.ui.statistic

import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.util.Range
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet


/**
 * Created by George Yu on 2020/2/23.
 */
class MyLineDataSet(yVals: List<Entry>, label: String?) : LineDataSet(yVals, label) {
    override fun getColor(index: Int): Int {
        return when (getEntryForIndex(index).y) {
            in Range(true, 1f, 2f, false) -> getColor(R.color.mood_very_bad)
            in Range(true, 2f, 3f, false) -> getColor(R.color.mood_bad)
            in Range(true, 3f, 4f, false) -> getColor(R.color.mood_normal)
            in Range(true, 4f, 5f, false) -> getColor(R.color.mood_good)
            else -> getColor(R.color.mood_very_good)
        }
    }
}
