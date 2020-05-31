package com.danteyu.studio.moodietrail.network

import android.net.LinkProperties
import android.net.NetworkCapabilities


/**
 * Created by George Yu on 2020/1/31.
 */
sealed class Event {
    abstract val state: NetworkState

    class ConnectivityEvent(override val state: NetworkState) : Event()
    class NetworkCapabilityEvent(override val state: NetworkState, val old: NetworkCapabilities?) : Event()
    class LinkPropertyChangeEvent(override val state: NetworkState, val old: LinkProperties?) : Event()
}