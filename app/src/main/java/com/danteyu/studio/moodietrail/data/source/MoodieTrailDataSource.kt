package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result


/**
 * Created by George Yu in Jan. 2020.
 *
 * Main entry point for accessing Stylish sources.
 */
interface MoodieTrailDataSource {

    suspend fun writeDownNote(note: Note): Result<Boolean>
}