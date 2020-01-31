package com.danteyu.studio.moodietrail.network

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities


/**
 * Created by George Yu on 2020/1/31.
 */
internal class NetworkStateImp : NetworkState {

    override var isConnected: Boolean = false
        set(value) {
            field = value
            NetworkEvents.notify(
                Event.ConnectivityEvent(
                    this
                )
            )
        }

    override var network: Network? = null

    override var linkProperties: LinkProperties? = null
        set(value) {
            val old = field
            field = value
            NetworkEvents.notify(
                Event.LinkPropertyChangeEvent(
                    this,
                    old
                )
            )
        }

    override var networkCapabilities: NetworkCapabilities? = null
        set(value) {
            val old = field
            field = value
            NetworkEvents.notify(
                Event.NetworkCapabilityEvent(
                    this,
                    old
                )
            )

        }
}
