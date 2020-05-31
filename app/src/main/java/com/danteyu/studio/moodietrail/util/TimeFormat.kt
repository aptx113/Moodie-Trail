package com.danteyu.studio.moodietrail.util

/**
 * Created by George Yu on 2020/3/3.
 */
enum class TimeFormat(val value: Int) {

    FORMAT_YYYY_MM_DD_HH_MM_SS(0x01),
    FORMAT_YYYY_MM_DD_E_HH_MM(0x02),
    FORMAT_YYYY_MM_DD_E(0x03),
    FORMAT_YYYY_MM_DD(0x04),
    FORMAT_YYYY_MM(0x05),
    FORMAT_MM_DD(0x06),
    FORMAT_DD(0x07),
    FORMAT_HH_MM(0x08)
}
