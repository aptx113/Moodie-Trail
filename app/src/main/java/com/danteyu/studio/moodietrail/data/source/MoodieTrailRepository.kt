package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result


/**
 * Created by George Yu in Jan. 2020.
 *
 * Interface to the Moodie Trail layers.
 */
interface MoodieTrailRepository {

    suspend fun writeDownNote(note: Note): Result<Boolean>
}