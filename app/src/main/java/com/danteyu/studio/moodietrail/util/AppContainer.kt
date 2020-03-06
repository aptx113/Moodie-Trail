package com.danteyu.studio.moodietrail.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.danteyu.studio.moodietrail.AlarmReceiver
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.sql.Timestamp
import java.util.*


/**
 * Created by George Yu on 2020/2/23.
 */
class AppContainer {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Util.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient =
        GoogleSignIn.getClient(MoodieTrailApplication.instance, gso)

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
        Logger.i("ThisMonthFirstDate = ${timestamp.toDisplayFormat(TimeFormat.FORMAT_YYYY_MM)}-01")
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

    fun setupAlarmManager() {
        val alarmManager =
            MoodieTrailApplication.instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(MoodieTrailApplication.instance, AlarmReceiver::class.java).let { intent ->
                intent.putExtra(
                    NOTIFICATION_KEY,
                    NOTIFICATION_INTENT_VALUE
                )
                PendingIntent.getBroadcast(
                    MoodieTrailApplication.instance,
                    ALARM_INTENT_REQUEST_CODE,
                    intent,
                    0
                )
            }

        // Set the alarm to start at 12:30 p.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, ALARM_CALENDAR_HOUR)
            set(Calendar.MINUTE, ALARM_CALENDAR_MINUTE)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 1 day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            ALARM_INTERVAL_MILLIS.toLong(),
            alarmIntent
        )
    }

    companion object {
        const val NOTIFICATION_KEY = "regular reminder"
        const val NOTIFICATION_INTENT_VALUE = "activity_app"
        const val ALARM_INTENT_REQUEST_CODE = 0
        const val ALARM_INTERVAL_MILLIS = 1000 * 60 * 60 * 24
        const val ALARM_CALENDAR_HOUR = 12
        const val ALARM_CALENDAR_MINUTE = 30

    }
}