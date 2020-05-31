package com.danteyu.studio.moodietrail

import android.app.Application
import com.danteyu.studio.moodietrail.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.NetworkStateHolder.registerConnectivityBroadcaster
import com.danteyu.studio.moodietrail.di.AppContainer
import com.danteyu.studio.moodietrail.di.ServiceLocator
import com.google.firebase.FirebaseApp
import kotlin.properties.Delegates


/**
 * Created by George Yu in Jan. 2020.
 *
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the Dependency Injection framework.
 */
class MoodieTrailApplication : Application() {

    // Depends on the flavor
    val moodieTrailRepository: MoodieTrailRepository
        get() = ServiceLocator.provideRepository(this)

    lateinit var appContainer: AppContainer

    companion object {
        var instance: MoodieTrailApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerConnectivityBroadcaster()

        FirebaseApp.initializeApp(instance)
        appContainer = AppContainer()
    }
}