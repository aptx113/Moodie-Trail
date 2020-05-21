package com.danteyu.studio.moodietrail.repository

import android.graphics.Bitmap
import com.danteyu.studio.moodietrail.data.*
import com.danteyu.studio.moodietrail.data.model.*
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


/**
 * Created by George Yu in Jan. 2020.
 *
 * Interface to the Moodie Trail layers.
 */
interface MoodieTrailRepository {

    suspend fun getNotesByDateRange(uid: String, startDate: Long, endDate: Long): Result<List<Note>>

    suspend fun getPsyTests(uid: String): Result<List<PsyTest>>

    suspend fun getAvgMoodScoresByDateRange(uid: String, startDate: Long, endDate: Long):Result<List<AverageMood>>

    suspend fun getConsultationCalls(): Result<List<ConsultationCall>>

    suspend fun getUserProfile(uid: String): Result<User>

    suspend fun signUpUser(user: User, id: String): Result<Boolean>

    suspend fun handleFacebookAccessToken(token: AccessToken):Result<Boolean>

    suspend fun firebaseAuthWithGoogle(acct: GoogleSignInAccount):Result<Boolean>

    suspend fun postNote(uid: String, note: Note): Result<Boolean>

    suspend fun postAvgMood(
        uid: String,
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean>

    suspend fun postPsyTest(uid:String, psyTest: PsyTest):Result<Boolean>

    suspend fun uploadNoteImage(uid:String, noteImage:Bitmap,date:String):Result<String>

    suspend fun updateNote(uid: String, editedNote: Note, noteId: String): Result<Boolean>

    suspend fun deleteNote(uid: String, note: Note): Result<Boolean>

    suspend fun deleteAvgMood(uid: String, avgMoodId:String): Result<Boolean>

    suspend fun deletePsyTest(uid: String, psyTest: PsyTest): Result<Boolean>
}