package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result


/**
 * Created by George Yu in Jan. 2020.
 *
 * Interface to the Moodie Trail layers.
 */
interface MoodieTrailRepository {

    suspend fun getNotes(): Result<List<Note>>

    suspend fun getNotesByDate(year:Int, month:Int, day:Int): Result<List<Note>>

    suspend fun getNotesByDateRange(startDate:Long, endDate:Long): Result<List<Note>>

    suspend fun writeDownNote(note: Note): Result<Boolean>

    suspend fun submitAvgMood(averageMood: AverageMood, timeList:String):Result<Boolean>

    suspend fun deleteNote(note: Note): Result<Boolean>
}