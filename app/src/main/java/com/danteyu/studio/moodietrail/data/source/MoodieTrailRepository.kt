package com.danteyu.studio.moodietrail.data.source

import com.danteyu.studio.moodietrail.data.*


/**
 * Created by George Yu in Jan. 2020.
 *
 * Interface to the Moodie Trail layers.
 */
interface MoodieTrailRepository {

    suspend fun getNotes(uid: String): Result<List<Note>>

    suspend fun getNotesByDateRange(uid: String, startDate: Long, endDate: Long): Result<List<Note>>

    suspend fun getPsyTests(uid: String): Result<List<PsyTest>>

    suspend fun getUserProfile(id: String): Result<User>

    suspend fun signUpUser(user: User, id: String): Result<Boolean>

    suspend fun postNote(uid: String, note: Note): Result<Boolean>

    suspend fun postAvgMood(
        uid: String,
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean>

    suspend fun deleteNote(uid: String, note: Note): Result<Boolean>
}