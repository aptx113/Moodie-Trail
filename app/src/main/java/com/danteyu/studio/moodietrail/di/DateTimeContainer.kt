package com.danteyu.studio.moodietrail.di

import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.TimeFormat
import java.sql.Timestamp
import java.util.*

/**
 * Created by George Yu on 2020/3/7.
 */
class DateTimeContainer {

    val calendar: Calendar = Calendar.getInstance()

    private fun getThisMonthLastDate(calendar: Calendar, currentDate: Long): Int {

        calendar.timeInMillis = currentDate
        calendar.add(Calendar.MONTH, 0)
        calendar.set(
            Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Function to get Start Time Of this Month in timestamp in milliseconds
     */
    fun getStartDateOfMonth(timestamp: Long): Long {

        val monthStart = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_daybegin,
                "${timestamp.toDisplayFormat(TimeFormat.FORMAT_YYYY_MM)}-01"
            )
        )
        Logger.i(
            "ThisMonthFirstDate = ${timestamp.toDisplayFormat(
                TimeFormat.FORMAT_YYYY_MM
            )}-01"
        )
        return monthStart.time
    }

    /**
     * Function to get End Time Of this Month in timestamp in milliseconds
     */
    fun getEndDateOfMonth(calendar: Calendar, timestamp: Long): Long {

        val monthEnd = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_dayend,
                "${timestamp.toDisplayFormat(TimeFormat.FORMAT_YYYY_MM)}-${getThisMonthLastDate(
                    calendar,
                    timestamp
                )}"
            )
        )
        Logger.i(
            "ThisMonthLastDate = ${timestamp.toDisplayFormat(TimeFormat.FORMAT_YYYY_MM)}-${getThisMonthLastDate(
                calendar,
                timestamp
            )}"
        )
        return monthEnd.time
    }

    /**
     * Function to get Start Time Of Day in timestamp in milliseconds
     */
    fun getStartTimeOfDay(timestamp: Long): Long {

        val dayStart = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_daybegin,
                timestamp.toDisplayFormat(TimeFormat.FORMAT_YYYY_MM_DD)
            )
        )
        return dayStart.time
    }

    /**
     * Function to get End Time Of Day in timestamp in milliseconds
     */
    fun getEndTimeOfDay(timestamp: Long): Long {

        val dayEnd = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_dayend,
                timestamp.toDisplayFormat(TimeFormat.FORMAT_YYYY_MM_DD)
            )
        )
        return dayEnd.time
    }

}