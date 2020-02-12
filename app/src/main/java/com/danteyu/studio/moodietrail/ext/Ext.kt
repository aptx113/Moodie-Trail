package com.danteyu.studio.moodietrail.ext

import android.app.Activity
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.network.Event
import com.danteyu.studio.moodietrail.network.NetworkConnectivityListener
import com.danteyu.studio.moodietrail.network.NetworkEvents
import com.danteyu.studio.moodietrail.network.NetworkState
import com.danteyu.studio.moodietrail.util.Util.getString
import java.text.Format
import java.util.*

/**
 * Created by George Yu in Jan. 2020.
 */

fun Long.toDisplayFormat(dateFormat: Int): String {

    return SimpleDateFormat(
        when (dateFormat) {
            FORMAT_MM_DD -> getString(
                R.string.simpledateformat_MM_dd
            )
            FORMAT_YYYY_MM -> getString(
                R.string.simpledateformat_yyyy_MM
            )
            FORMAT_YYYY_MM_DD -> getString(
                R.string.simpledateformat_yyyy_MM_dd
            )
            FORMAT_YYYY_MM_DD_E -> getString(
                R.string.simpledatefromat_yyyy_MM_dd_E
            )
            FORMAT_YYYY_MM_DD_HH_MM -> getString(R.string.simpledateformat_yyyy_MM_dd_HH_mm)
            Format_YYYY_MM_DD_HH_MM_LIST -> getString(R.string.time_list_format)

            FORMAT_HH_MM -> getString(
                R.string.simpledateformat_HH_mm
            )

            else -> null
        }
        , Locale.TAIWAN
    ).format(this)

}

//fun Long.toDisplayFormat(): String {
//    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this)
//}

// Increase touch area of the view/button .
fun View.setTouchDelegate() {
    val parent = this.parent as View  // button: the view you want to enlarge hit area
    parent.post {
        val rect = Rect()
        this.getHitRect(rect)
        rect.top -= 100    // increase top hit area
        rect.left -= 100   // increase left hit area
        rect.bottom += 100 // increase bottom hit area
        rect.right += 100  // increase right hit area
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}


internal object Constants {
    const val ID_KEY = "network.monitoring.previousState"
}

internal fun NetworkConnectivityListener.onListenerCreated() {

    NetworkEvents.observe(this as LifecycleOwner, Observer {
        if (previousState != null)
            networkConnectivityChanged(it)
    })

}

internal fun NetworkConnectivityListener.onListenerResume(networkState: NetworkState) {
    if (!shouldBeCalled || !checkOnResume) return

    val previousState = previousState
    val isConnected = networkState.isConnected

    this.previousState = isConnected

    val connectionLost = (previousState == null || previousState == true) && !isConnected
    val connectionBack = previousState == false && isConnected

    if (connectionLost || connectionBack) {
        networkConnectivityChanged(Event.ConnectivityEvent(networkState))
    }

}

/**
 * This property serves as a flag to detect if this activity lost network
 */
internal var NetworkConnectivityListener.previousState: Boolean?
    get() {
        return when {
            this is Fragment -> this.arguments?.previousState
            this is Activity -> this.intent.extras?.previousState
            else -> null
        }
    }
    set(value) {
        when {
            this is Fragment -> {
                val a = this.arguments ?: Bundle()
                a.previousState = value
                this.arguments = a
            }
            this is Activity -> {
                val a = this.intent.extras ?: Bundle()
                a.previousState = value
                this.intent.replaceExtras(a)
            }
        }
    }
internal var Bundle.previousState: Boolean?
    get() = when (getInt(Constants.ID_KEY, -1)) {
        -1 -> null
        0 -> false
        else -> true
    }
    set(value) {
        putInt(Constants.ID_KEY, if (value == true) 1 else 0)
    }

const val FORMAT_MM_DD: Int = 0x01
const val FORMAT_YYYY_MM_DD_E: Int = 0x02
const val FORMAT_YYYY_MM_DD: Int = 0x03
const val FORMAT_YYYY_MM: Int = 0x04
const val FORMAT_HH_MM: Int = 0x05
const val FORMAT_YYYY_MM_DD_HH_MM = 0x06
const val Format_YYYY_MM_DD_HH_MM_LIST = 0x07
