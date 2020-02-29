package com.danteyu.studio.moodietrail.util

import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.ext.FORMAT_YYYY_MM
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.google.android.gms.auth.api.signin.GoogleSignIn
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

    val googleSignInClient = GoogleSignIn.getClient(MoodieTrailApplication.instance, gso)

    /**
     * Function to get Start Time Of Date in timestamp in milliseconds
     */
    fun getStartDateOfMonth(timestamp: Long): Long? {

        val dayStart = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_daybegin,
                "${timestamp.toDisplayFormat(FORMAT_YYYY_MM)}-01"
            )
        )
        Logger.i("ThisMonthFirstDate = ${timestamp.toDisplayFormat(FORMAT_YYYY_MM)}-01")
        return dayStart.time
    }
}