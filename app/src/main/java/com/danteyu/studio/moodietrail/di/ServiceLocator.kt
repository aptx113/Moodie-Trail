package com.danteyu.studio.moodietrail.di

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.danteyu.studio.moodietrail.repository.DefaultMoodieTrailRepository
import com.danteyu.studio.moodietrail.data.source.MoodieTrailDataSource
import com.danteyu.studio.moodietrail.data.source.local.MoodieTrailLocalDataSource
import com.danteyu.studio.moodietrail.data.source.remote.MoodieTrailRemoteDataSource
import com.danteyu.studio.moodietrail.repository.MoodieTrailRepository


/**
 * Created by George Yu in Jan. 2020.
 *
 * A Service Locator for the [MoodieTrailRepository].
 */
object ServiceLocator {

    @Volatile
    var moodieTrailRepository: MoodieTrailRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): MoodieTrailRepository {
        synchronized(this) {
            return moodieTrailRepository
                ?: createMoodieTrailRepository(
                    context
                )
        }
    }

    private fun createMoodieTrailRepository(context: Context): MoodieTrailRepository {
        return DefaultMoodieTrailRepository(
            MoodieTrailRemoteDataSource,
            createMoodieTrailLocalDataSource(
                context
            )
        )
    }

    private fun createMoodieTrailLocalDataSource(context: Context): MoodieTrailDataSource {
        return MoodieTrailLocalDataSource(context)
    }
}