package com.danteyu.studio.moodietrail

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.danteyu.studio.moodietrail.data.model.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ext.sendNotification
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.ui.login.UserManager
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.TimeFormat
import com.danteyu.studio.moodietrail.util.Util.createChannel
import com.danteyu.studio.moodietrail.util.Util.getCalendar
import com.danteyu.studio.moodietrail.util.Util.getEndTimeOfDay
import com.danteyu.studio.moodietrail.util.Util.getStartTimeOfDay
import com.danteyu.studio.moodietrail.util.Util.getString
import com.danteyu.studio.moodietrail.util.Util.setupAlarmManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.StringBuilder

/**
 * Created by George Yu on 2020/3/1.
 */
class AlarmReceiver : BroadcastReceiver() {

    private val notesToday = MutableLiveData<List<Note>>()
    private var repository: MoodieTrailRepository? = null
    private val timeInMillisecond = getCalendar().timeInMillis
    private var dailyTextContent = ""
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult: PendingResult = goAsync()
        val asyncTask = Task(pendingResult, intent)
        asyncTask.execute()

        repository = (context.applicationContext as MoodieTrailApplication).moodieTrailRepository

        intent.extras?.let {

            if (it.get(RECEIVE_KEY) == RECEIVE_INTENT_VALUE
                && getCalendar().timeInMillis.toDisplayFormat(
                    TimeFormat.FORMAT_HH_MM
                ).split(":")[0] == ALARM_START_HOUR
            ) {
                UserManager.id?.let {
                    getNotesTodayToDecideMessage(
                        it,
                        getStartTimeOfDay(timeInMillisecond),
                        getEndTimeOfDay(timeInMillisecond)
                    )
                }

            }
        }

        if (intent.action == INTENT_ACTION_BOOT_COMPLETED
            || intent.action == INTENT_ACTION_TIME_TIMEZONE_CHANGED
            || intent.action == INTENT_ACTION_TIME_SET
        ) {
            setupAlarmManager()
        }

        createChannel(
            getString(R.string.daily_notification_channel_id),
            getString(R.string.daily_notification)
        )

    }

    private class Task(
        private val pendingResult: PendingResult, private val intent: Intent
    ) : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Action: ${intent.action}\n")
            stringBuilder.append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            return toString().also { log ->
                Logger.d(log)
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish()
        }
    }

    private fun getNotesTodayToDecideMessage(uid: String, startDate: Long, endDate: Long) {
        coroutineScope.launch {

            val result = repository?.getNotesByDateRange(uid, startDate, endDate)

            notesToday.value = when (result) {
                is Result.Success -> {
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
            notesToday.value?.let { decideMessageThenSendNotification(it) }
        }
    }

    private fun decideMessageThenSendNotification(notesToday: List<Note>) {

        dailyTextContent = if (notesToday.isNullOrEmpty()) {
            getString(R.string.how_do_you_feel_today)
        } else {
            MoodieTrailApplication.instance.getString(
                R.string.today_notes_size,
                notesToday.size
            )
        }

        val notificationManager = ContextCompat.getSystemService(
            MoodieTrailApplication.instance, NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            dailyTextContent,
            MoodieTrailApplication.instance
        )
    }

    companion object {
        const val ALARM_START_HOUR = "12"
        const val RECEIVE_KEY = "regular reminder"
        const val RECEIVE_INTENT_VALUE = "activity_app"
        const val INTENT_ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
        const val INTENT_ACTION_TIME_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED"
        const val INTENT_ACTION_TIME_SET = "android.intent.action.TIME_SET"

    }
}
