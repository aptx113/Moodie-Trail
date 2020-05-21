package com.danteyu.studio.moodietrail.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/1/31.
 */
@Parcelize
data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var picture: String = ""
) : Parcelable