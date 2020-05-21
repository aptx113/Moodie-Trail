package com.danteyu.studio.moodietrail.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/1/31.
 */
@Parcelize
data class PsyTest(
    var id: String = "",
    var itemSleep: Float = -1f,
    var itemAnxiety: Float = -1f,
    var itemAnger: Float = -1f,
    var itemDepression: Float = -1f,
    var itemInferiority: Float = -1f,
    var itemSuicide: Float = -1f,
    var totalScore: Float = itemSleep + itemAnxiety + itemAnger + itemDepression + itemInferiority,
    var createdTime: Long = 0L
) : Parcelable