package com.danteyu.studio.moodietrail.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import java.util.*


/**
 * Created by George Yu in Jan. 2020.
 */
object Util {

    /**
     * Determine and monitor the connectivity status
     *
     * https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
     */
    fun isInternetConnected(): Boolean {
        val cm = MoodieTrailApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return MoodieTrailApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return MoodieTrailApplication.instance.getColor(resourceId)
    }

    fun getDrawable(resourceId: Int): Drawable? {
        return MoodieTrailApplication.instance.getDrawable(resourceId)
    }

    fun getCalendar(): Calendar {
        return MoodieTrailApplication.instance.appContainer.calendar
    }

    fun getAuth(): FirebaseAuth {
        return MoodieTrailApplication.instance.appContainer.auth
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return MoodieTrailApplication.instance.appContainer.googleSignInClient
    }

    fun getStartDateOfMonth(timestamp: Long): Long? {
        return MoodieTrailApplication.instance.appContainer.getStartDateOfMonth(timestamp)
    }

    fun getStartTimeOfDay(timestamp: Long): Long? {
        return MoodieTrailApplication.instance.appContainer.getStartTimeOfDay(timestamp)
    }

    fun getEndTimeOfDay(timestamp: Long): Long? {
        return MoodieTrailApplication.instance.appContainer.getEndTimeOfDay(timestamp)
    }

    fun setupAlarmManager() {
        return MoodieTrailApplication.instance.appContainer.setupAlarmManager()
    }
}