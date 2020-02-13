package com.danteyu.studio.moodietrail.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/1/31.
 */
@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val notes: List<Note> = listOf(),
    val tests: List<Test> = listOf(),
    val averageMood: List<AverageMood> = listOf()
) : Parcelable