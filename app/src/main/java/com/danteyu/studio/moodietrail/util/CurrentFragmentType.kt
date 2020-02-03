package com.danteyu.studio.moodietrail.util

import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.util.Util.getString


/**
 * Created by George Yu in Jan. 2020.
 */
enum class CurrentFragmentType(val value: String) {
    NOTE(getString(R.string.note)),
    STATISTIC(getString(R.string.statistic)),
    TESTRESULT(getString(R.string.test_result)),
    PROFILE(getString(R.string.profile)),
    RECORDMOOD("")
}