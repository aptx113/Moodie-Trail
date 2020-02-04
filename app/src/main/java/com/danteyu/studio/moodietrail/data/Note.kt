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
    val createdTime: Long = -1,
    val mood: Int = -1,
    val emotions: List<Int> = listOf(),
    val symptoms: List<Int> = listOf(),
    val content: String = "",
    val location: Location? = null,
    val image: String? = null,
    val tags: List<String> = listOf()

) : Parcelable