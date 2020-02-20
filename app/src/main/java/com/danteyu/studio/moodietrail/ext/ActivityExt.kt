package com.danteyu.studio.moodietrail.ext

import android.app.Activity
import android.os.Message
import android.view.Gravity
import android.widget.Toast
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.factory.ViewModelFactory


/**
 * Created by George Yu in Jan. 2020.
 *
 * Extension functions for Activity.
 */
fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as MoodieTrailApplication).moodieTrailRepository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        show()
    }
}

fun Activity?.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
        show()
    }
}

fun Activity?.showToastCenter(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}