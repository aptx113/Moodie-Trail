package com.danteyu.studio.moodietrail.ext

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.danteyu.studio.moodietrail.MainActivity
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.model.Note
import com.danteyu.studio.moodietrail.util.Util
import com.danteyu.studio.moodietrail.util.Util.getString

/**
 * Created by George Yu on 2020/5/7.
 */

private const val NOTIFICATION_ID_DAILY = 1
private const val REQUEST_CODE_DAILY = 1
private const val FLAG_DAILY = 1

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    val bundle = bundleOf("noteKey" to Note())

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent =
        PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE_DAILY,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    val recordMoodPendingIntent =
        NavDeepLinkBuilder(MoodieTrailApplication.instance).setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation).setDestination(R.id.recordMoodFragment)
            .setArguments(bundle)
            .createPendingIntent()

    val builder = NotificationCompat.Builder(
        applicationContext, getString(R.string.daily_notification_channel_id)
    ).setSmallIcon(R.drawable.ic_moodie_trail)
        .setContentTitle(getString(R.string.daily_notification_title))
        .setContentText(messageBody)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setContentIntent(recordMoodPendingIntent)
        .setAutoCancel(true)
        .setColor(Util.getColor(R.color.blue_700))

    notify(NOTIFICATION_ID_DAILY, builder.build())
}

/**
 * Cancels all notifications.
 */
fun NotificationManager.cancelNotification() {
    cancelAll()
}