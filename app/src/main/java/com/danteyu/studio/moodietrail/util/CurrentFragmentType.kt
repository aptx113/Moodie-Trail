package com.danteyu.studio.moodietrail.util

import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.util.Util.getString


/**
 * Created by George Yu in Jan. 2020.
 */
enum class CurrentFragmentType(val value: String) {
    LOGIN(""),
    HOME(getString(R.string.note)),
    STATISTIC(getString(R.string.statistic)),
    PSYTESTRECORD(getString(R.string.psy_test_record)),
    PROFILE(getString(R.string.profile)),
    RECORDMOOD(""),
    RECORDDETAIL(""),
    PSYTEST(""),
    PSYTESTBODY("")

}