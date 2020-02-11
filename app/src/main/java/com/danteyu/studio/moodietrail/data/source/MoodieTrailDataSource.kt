package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result


/**
 * Created by George Yu in Jan. 2020.
 *
 * Main entry point for accessing Stylish sources.
 */
interface MoodieTrailDataSource {

    suspend fun getNotes(): Result<List<Note>>

    suspend fun getNotesByDate(year:Int, month:Int, day:Int): Result<List<Note>>

    suspend fun writeDownNote(note: Note): Result<Boolean>

    suspend fun submitAvgMood(averageMood: AverageMood):Result<Boolean>

    suspend fun deleteNote(note: Note): Result<Boolean>
}