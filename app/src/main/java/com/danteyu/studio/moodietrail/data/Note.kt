package com.danteyu.studio.moodietrail.data

import android.location.Location
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by George Yu on 2020/1/31.
 */
@Parcelize
data class Note(
    var id: String = "",
    var date: Long = 0,
    var weekOfMonth: Int = 0,
    var mood: Int = 0,
    var emotions: List<Int>? = null,
    var feelings: List<Int>? = null,
    var content: String? = null,
    var location: Location? = null,
    var image: String? = null,
    var tags: MutableList<String>? = mutableListOf()

) : Parcelable