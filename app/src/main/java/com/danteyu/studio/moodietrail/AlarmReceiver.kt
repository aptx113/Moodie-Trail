package com.danteyu.studio.moodietrail

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDeepLinkBuilder
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.TimeFormat
import com.danteyu.studio.moodietrail.util.Util.getCalendar
import com.danteyu.studio.moodietrail.util.Util.getColor
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
    private var regularTextTitle: String? = ""
    private var regularTextContent: String? = ""
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
                setMessage()
            }
        }

        if (intent.action == INTENT_ACTION_BOOT_COMPLETED
            || intent.action == INTENT_ACTION_TIME_TIMEZONE_CHANGED
            || intent.action == INTENT_ACTION_TIME_SET
        ) {
            setupAlarmManager()
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        val intent: Intent? =
            Intent(MoodieTrailApplication.instance, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(MoodieTrailApplication.instance, 0, intent, 0)

        val bundle = bundleOf("noteKey" to Note())
        val pendingIntentForNavToFragment =
            NavDeepLinkBuilder(MoodieTrailApplication.instance).setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.navigation).setDestination(R.id.recordMoodFragment)
                .setArguments(bundle)
                .createPendingIntent()

        val builder = NotificationCompat.Builder(MoodieTrailApplication.instance, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_moodie_trail)
            .setContentTitle(regularTextTitle)
            .setContentText(regularTextContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            // Set the intent that will fire when the user taps the notification
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(regularTextContent)
            )
            .setContentIntent(pendingIntentForNavToFragment)
            .setAutoCancel(true)
            .setColor(getColor(R.color.blue_700))

        return builder
    }

    private fun createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification)
            val descriptionText = getString(R.string.remind_record_mood)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                MoodieTrailApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(MoodieTrailApplication.instance)) {
            // notificationId is a unique int for each notification that you must define
            notify(REGULAR_NOTIFICATION_ID, createNotification().build())
        }
    }

    private fun setMessage() {
        UserManager.id?.let {
            getStartTimeOfDay(timeInMillisecond)?.let { startTime ->
                getEndTimeOfDay(timeInMillisecond)?.let { endTime ->
                    getNotesTodayToDecideMessage(
                        it,
                        startTime,
                        endTime
                    )
                }
            }
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
            notesToday.value?.let { decideMessageContent(it) }
        }
    }

    private fun decideMessageContent(notesToday: List<Note>) {

        regularTextTitle =
            MoodieTrailApplication.instance.getString(R.string.remind_note_notification_title)
        regularTextContent = if (notesToday.isEmpty()) {
            getString(R.string.how_do_you_feel_today)
        } else {
            MoodieTrailApplication.instance.getString(
                R.string.today_notes_size,
                notesToday.size
            )
        }

        if (regularTextTitle.equals(getString(R.string.remind_note_notification_title))) {

            createNotificationChannel()
        }
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

    companion object {
        const val ALARM_START_HOUR = "12"
        const val RECEIVE_KEY = "regular reminder"
        const val RECEIVE_INTENT_VALUE = "activity_app"
        const val INTENT_ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
        const val INTENT_ACTION_TIME_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED"
        const val INTENT_ACTION_TIME_SET = "android.intent.action.TIME_SET"
        const val CHANNEL_ID = "MoodieTrail"
        const val REGULAR_NOTIFICATION_ID = 0

    }
}