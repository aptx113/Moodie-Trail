package com.danteyu.studio.moodietrail.network

import androidx.lifecycle.LiveData

/**
 * Created by George Yu on 2020/1/31.
 *
 * This liveData enabling network connectivity monitoring
 * @see NetworkStateHolder to get current connection state
 */
object NetworkEvents : LiveData<Event>() {

    internal fun notify(event: Event) {
        postValue(event)
    }

}