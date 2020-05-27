package com.danteyu.studio.moodietrail.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/2/10.
 */

@Parcelize
data class AverageMood(
    var idTime: String = "",
    var score: Float = 0.0f,
    var time: Long = 0
) : Parcelable
