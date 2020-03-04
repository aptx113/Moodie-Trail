package com.danteyu.studio.moodietrail

import android.app.AlarmManager
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
import java.util.*

/**
 * Created by George Yu on 2020/3/1.
 */
class AlarmReceiver : BroadcastReceiver() {

    private val notesToday = MutableLiveData<List<Note>>()
    private var repository: MoodieTrailRepository? = null
    private val timeInMillisecond = getCalendar().timeInMillis
    private var alarmManager: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult: PendingResult = goAsync()
        val asyncTask = Task(pendingResult, intent)
        asyncTask.execute()

        repository = (context.applicationContext as MoodieTrailApplication).moodieTrailRepository

        intent.extras?.let {

            if (it.get(receiveKey) == receiveIntentValue
                && getCalendar().timeInMillis.toDisplayFormat(
                    TimeFormat.FORMAT_HH_MM
                ).split(":")[0] == "12"
            ) {
                setMessage()
            }
        }

        if (intent.action == "android.intent.action.BOOT_COMPLETED"){

            setupAlarmManager()
        }
    }

    private fun createNotificationChannel() {

        val intent: Intent? =
            Intent(MoodieTrailApplication.instance, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }


        val bundle = bundleOf("noteKey" to Note())
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(MoodieTrailApplication.instance, 0, intent, 0)
        val pendingIntentForNavToFragment =
            NavDeepLinkBuilder(MoodieTrailApplication.instance).setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.navigation).setDestination(R.id.recordMoodFragment)
                .setArguments( bundle)
                .createPendingIntent()

        val notificationId = 0

        val builder = NotificationCompat.Builder(MoodieTrailApplication.instance, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_moodie_trail)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            // Set the intent that will fire when the user taps the notification
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textContent)
            )
            .setContentIntent(pendingIntentForNavToFragment)
            .setAutoCancel(true)
            .setColor(getColor(R.color.blue_700))

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
            notify(notificationId, builder.build())
        }
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private fun setMessage() {
        UserManager.id?.let {
            getNotesTodayToDecideMessage(
                it,
                getStartTimeOfDay(timeInMillisecond)!!,
                getEndTimeOfDay(timeInMillisecond)!!
            )
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

            determineMessageContent(notesToday.value!!)
        }
    }

    private fun determineMessageContent(notesToday: List<Note>) {

        textTitle =
            MoodieTrailApplication.instance.getString(R.string.remind_note_notification_title)
        textContent = if (notesToday.isEmpty()) {
            getString(R.string.how_do_you_feel_today)
        } else {
            MoodieTrailApplication.instance.getString(
                R.string.today_notes_size,
                notesToday.size
            )
        }

        if (textTitle.equals(getString(R.string.remind_note_notification_title))) {

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
        const val receiveKey = "regular reminder"
        const val receiveIntentValue = "activity_app"

        private var textTitle: String? = ""
        private var textContent: String? = ""
        const val CHANNEL_ID = "MoodieTrail"

    }
}