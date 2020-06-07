package com.danteyu.studio.moodietrail.data.source.local

import android.content.Context
import android.graphics.Bitmap
import com.danteyu.studio.moodietrail.data.*
import com.danteyu.studio.moodietrail.data.model.*
import com.danteyu.studio.moodietrail.data.source.MoodieTrailDataSource
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


/**
 * Created by George Yu in Jan. 2020.
 *
 * Concrete implementation of a Moodie Trail source as a db.
 */
class MoodieTrailLocalDataSource(val context: Context) : MoodieTrailDataSource {

    override suspend fun getNotesByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<Note>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPsyTests(uid: String): Result<List<PsyTest>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAvgMoodByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<AverageMood>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getConsultationCalls(): Result<List<ConsultationCall>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserProfile(uid: String): Result<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun signUpUser(user: User, id: String): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun handleFacebookAccessToken(token: AccessToken): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun firebaseAuthWithGoogle(acct: GoogleSignInAccount): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun postNote(uid: String, note: Note): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun postAvgMood(
        uid: String,
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun postPsyTest(uid: String, psyTest: PsyTest): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun uploadNoteImage(
        uid: String,
        noteImage: Bitmap,
        date: String
    ): Result<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateNote(
        uid: String,
        editedNote: Note,
        noteId: String
    ): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteNote(uid: String, note: Note): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteAvgMood(uid: String, avgMoodId: String): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deletePsyTest(uid: String, psyTest: PsyTest): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
