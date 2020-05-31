package com.danteyu.studio.moodietrail.network

import com.danteyu.studio.moodietrail.util.Logger

/**
 * Created by George Yu on 2020/1/31.
 *
 * just like runCatching but without result
 * @see runCatching
 */
internal inline fun <T> T.safeRun(TAG: String = "", block: T.() -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        //ignore but log it
        Logger.e(e.toString())
    }
}