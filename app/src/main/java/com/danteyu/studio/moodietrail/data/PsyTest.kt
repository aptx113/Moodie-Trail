package com.danteyu.studio.moodietrail.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/1/31.
 */
@Parcelize
data class PsyTest(
    var id: String = "",
    var itemSleep: Int = -1,
    var itemAnxiety: Int = -1,
    var itemAnger: Int = -1,
    var itemDepression: Int = -1,
    var itemInferiority: Int = -1,
    var itemSuicide: Int = -1,
    var totalScore: Int = -1,
    var createdTime: Long = 0
) : Parcelable