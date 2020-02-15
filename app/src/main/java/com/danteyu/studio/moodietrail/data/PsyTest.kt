package com.danteyu.studio.moodietrail.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/1/31.
 */
@Parcelize
data class PsyTest(
    val id: String = "",
    val item1: Int = -1,
    val item2: Int = -1,
    val item3: Int = -1,
    val item4: Int = -1,
    val item5: Int = -1,
    val item6: Int = -1,
    val totalScore: Int = -1,
    val createdTime: Long = -1
) : Parcelable