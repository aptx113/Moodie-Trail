package com.danteyu.studio.moodietrail.data

import android.location.Location
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by George Yu on 2020/1/31.
 */
@Parcelize
data class Note(
    val id: String = "",
    val date: Long = -1,
    val createdTime: Long = -1,
    val time: Long = -1,
    var mood: Int = -1,
    val emotions: List<Int>? = null,
    val feelings: List<Int>? = null,
    val content: String? = null,
    val location: Location? = null,
    val image: String? = null,
    val tags: List<String>? = null

) : Parcelable