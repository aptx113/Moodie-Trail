package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.User


/**
 * Created by George Yu in Jan. 2020.
 *
 * Interface to the Moodie Trail layers.
 */
interface MoodieTrailRepository {

    suspend fun getNotes(uid:String): Result<List<Note>>

    suspend fun getNotesByDate(year:Int, month:Int, day:Int): Result<List<Note>>

    suspend fun getNotesByDateRange(uid:String, startDate:Long, endDate:Long): Result<List<Note>>

    suspend fun getUserProfile(id:String):Result<User>

    suspend fun writeDownNote(uid:String, note: Note): Result<Boolean>

    suspend fun registerUser(user: User,id: String):Result<Boolean>

    suspend fun submitAvgMood(uid:String, averageMood: AverageMood, timeList:String):Result<Boolean>

    suspend fun deleteNote(uid:String, note: Note): Result<Boolean>
}