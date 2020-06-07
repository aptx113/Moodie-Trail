package com.danteyu.studio.moodietrail.network

/**
 * Created by George Yu on 2020/1/31.
 *
 * When implemented by an Activity, it add hooks to network events thanks to ActivityLifecycleCallbacks
 * @see android.app.Application.ActivityLifecycleCallbacks
 */
interface NetworkConnectivityListener {

    /**
     * Put this at false to disable hooks ( enabled by default )
     */
    val shouldBeCalled: Boolean
        get() = true

    /**
     * Put this at false to disable hooks on resume ( enabled by default )
     */
    val checkOnResume: Boolean
        get() = true

    val isConnected: Boolean
        get() = NetworkStateHolder.isConnected


    fun networkConnectivityChanged(event: Event)
}
