package com.danteyu.studio.moodietrail.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/3/7.
 */
@Parcelize
data class PhoneConsultingInst(
    var id: String = "",
    var logo: String = "",
    var title: String = "",
    var phoneFee: String = "",
    var serviceHour: String = ""
) : Parcelable