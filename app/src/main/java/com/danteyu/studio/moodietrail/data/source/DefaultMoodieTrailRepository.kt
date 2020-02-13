package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result


/**
 * Created by George Yu in Jan. 2020.
 *
 * Concrete implementation to load Moodie Trail sources.
 */
class DefaultMoodieTrailRepository(
    private val remoteDataSource: MoodieTrailDataSource,
    private val moodieTrailLocalDataSource: MoodieTrailDataSource
) : MoodieTrailRepository {

    override suspend fun getNotes(): Result<List<Note>> {
        return remoteDataSource.getNotes()
    }

    override suspend fun getNotesByDate(year: Int, month: Int, day: Int): Result<List<Note>> {
        return remoteDataSource.getNotesByDate(year, month, day)
    }

    override suspend fun getNotesByDateRange(startDate: Long, endDate: Long): Result<List<Note>> {
        return remoteDataSource.getNotesByDateRange(startDate, endDate)
    }

    override suspend fun writeDownNote(note: Note): Result<Boolean> {
        return remoteDataSource.writeDownNote(note)
    }

    override suspend fun submitAvgMood(
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean> {
        return remoteDataSource.submitAvgMood(averageMood, timeList)
    }

    override suspend fun deleteNote(note: Note): Result<Boolean> {
        return remoteDataSource.deleteNote(note)
    }
}