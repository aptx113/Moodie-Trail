package com.danteyu.studio.moodietrail.data.source.local

import android.content.Context
import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailDataSource


/**
 * Created by George Yu in Jan. 2020.
 *
 * Concrete implementation of a Moodie Trail source as a db.
 */
class MoodieTrailLocalDataSource(val context: Context) : MoodieTrailDataSource {

    override suspend fun getNotes(): Result<List<Note>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNotesByDate(year: Int, month: Int, day: Int): Result<List<Note>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getNotesByDateRange(startDate: Long, endDate: Long): Result<List<Note>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun writeDownNote(note: Note): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun submitAvgMood(averageMood: AverageMood, timeList:String): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteNote(note: Note): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}