package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.source.local.MoodieTrailLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


/**
 * Created by George Yu in Jan. 2020.
 *
 * Concrete implementation to load Moodie Trail sources.
 */
class DefaultMoodieTrailRepository(
    private val moodieTrailRemoteDataSource: MoodieTrailDataSource,
    private val moodieTrailLocalDataSource: MoodieTrailDataSource
) : MoodieTrailRepository {}