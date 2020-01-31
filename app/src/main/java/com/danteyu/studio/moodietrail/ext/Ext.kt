package com.danteyu.studio.moodietrail.ext

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.network.Event
import com.danteyu.studio.moodietrail.network.NetworkConnectivityListener
import com.danteyu.studio.moodietrail.network.NetworkEvents
import com.danteyu.studio.moodietrail.network.NetworkState

/**
 * Created by George Yu in Jan. 2020.
 *
 * Increase touch area of the view/button .
 */
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