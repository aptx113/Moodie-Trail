package com.danteyu.studio.moodietrail.data.source

import android.graphics.Bitmap
import com.danteyu.studio.moodietrail.data.*


/**
 * Created by George Yu in Jan. 2020.
 *
 * Concrete implementation to load Moodie Trail sources.
 */
class DefaultMoodieTrailRepository(
    private val remoteDataSource: MoodieTrailDataSource,
    private val moodieTrailLocalDataSource: MoodieTrailDataSource
) : MoodieTrailRepository {

    override suspend fun getNotes(uid: String): Result<List<Note>> {
        return remoteDataSource.getNotes(uid)
    }

    override suspend fun getNotesByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<Note>> {
        return remoteDataSource.getNotesByDateRange(uid, startDate, endDate)
    }

    override suspend fun getPsyTests(uid: String): Result<List<PsyTest>> {
        return remoteDataSource.getPsyTests(uid)
    }

    override suspend fun getUserProfile(id: String): Result<User> {
        return remoteDataSource.getUserProfile(id)
    }

    override suspend fun getAvgMoodByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<AverageMood>> {
        return remoteDataSource.getAvgMoodByDateRange(uid, startDate, endDate)
    }

    override suspend fun signUpUser(user: User, id: String): Result<Boolean> {
        return remoteDataSource.signUpUser(user, id)
    }

    override suspend fun postNote(uid: String, note: Note): Result<Boolean> {
        return remoteDataSource.postNote(uid, note)
    }

    override suspend fun postAvgMood(
        uid: String,
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean> {
        return remoteDataSource.postAvgMood(uid, averageMood, timeList)
    }

    override suspend fun postPsyTest(uid: String, psyTest: PsyTest): Result<Boolean> {
        return remoteDataSource.postPsyTest(uid, psyTest)
    }

    override suspend fun uploadNoteImage(
        uid: String,
        noteImage: Bitmap,
        date: String
    ): Result<String> {
        return remoteDataSource.uploadNoteImage(uid, noteImage, date)
    }

    override suspend fun updateNote(
        uid: String,
        editedNote: Note,
        noteId: String
    ): Result<Boolean> {
        return remoteDataSource.updateNote(uid, editedNote, noteId)
    }

    override suspend fun deleteNote(uid: String, note: Note): Result<Boolean> {
        return remoteDataSource.deleteNote(uid, note)
    }

    override suspend fun deleteAvgMood(uid: String, avgMoodId:String): Result<Boolean> {
        return remoteDataSource.deleteAvgMood(uid, avgMoodId)
    }

    override suspend fun deletePsyTest(uid: String, psyTest: PsyTest): Result<Boolean> {
        return remoteDataSource.deletePsyTest(uid, psyTest)
    }
}