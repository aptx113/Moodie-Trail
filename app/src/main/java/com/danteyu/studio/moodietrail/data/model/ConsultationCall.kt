package com.danteyu.studio.moodietrail.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by George Yu on 2020/3/7.
 */
@Parcelize
data class ConsultationCall(
    var id: Int = 0,
    var logo: String = "",
    var name: String = "",
    var clientele: String = "",
    var phoneNumber: String = "",
    var callCharge: String = "",
    var serviceHour: String = ""
) : Parcelable