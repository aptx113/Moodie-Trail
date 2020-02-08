package com.danteyu.studio.moodietrail

import android.app.Application
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.NetworkStateHolder.registerConnectivityBroadcaster
import com.danteyu.studio.moodietrail.util.ServiceLocator
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by George Yu in Jan. 2020.
 *
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 */
class MoodieTrailApplication : Application() {

    // Depends on the flavor
    val moodieTrailRepository: MoodieTrailRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var instance: MoodieTrailApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerConnectivityBroadcaster()
    }
}