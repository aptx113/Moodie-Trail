package com.danteyu.studio.moodietrail.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/2/10.
 */

@Parcelize
data class AverageMood(
    var id: String = "",
    var avgMoodScore: Float = 0.0f,
    var year: Int = 0,
    var month: Int = 0,
    var dayOfMonth: Int = 0,
    var timeList:String =""
) : Parcelable