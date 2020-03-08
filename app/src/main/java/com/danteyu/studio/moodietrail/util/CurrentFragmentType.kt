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
    PSY_TEST_RECORD(getString(R.string.psy_test_record)),
    PROFILE(getString(R.string.profile)),
    RECORD_MOOD(""),
    RECORD_DETAIL(""),
    PSY_TEST(""),
    PSY_TEST_BODY(getString(R.string.bsrs_5)),
    PSY_TEST_RESULT(getString(R.string.psy_test_result)),
    PSY_TEST_RATING(getString(R.string.psy_test_rating)),
    PHONE_CONSULTING(getString(R.string.phone_consulting))

}