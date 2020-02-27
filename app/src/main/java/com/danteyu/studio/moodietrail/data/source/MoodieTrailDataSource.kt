package com.danteyu.studio.moodietrail.data.source

import android.graphics.Bitmap
import com.danteyu.studio.moodietrail.data.*
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult


/**
 * Created by George Yu in Jan. 2020.
 *
 * Main entry point for accessing Stylish sources.
 */
interface MoodieTrailDataSource {

    suspend fun getNotes(uid: String): Result<List<Note>>

    suspend fun getNotesByDateRange(uid: String, startDate: Long, endDate: Long): Result<List<Note>>

    suspend fun getPsyTests(uid: String): Result<List<PsyTest>>

    suspend fun getAvgMoodByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<AverageMood>>

    suspend fun getUserProfile(id: String): Result<User>

    suspend fun signUpUser(user: User, id: String): Result<Boolean>

    suspend fun handleFacebookAccessToken(token: AccessToken):Result<Boolean>

    suspend fun firebaseAuthWithGoogle(acct: GoogleSignInAccount):Result<Boolean>

    suspend fun postNote(uid: String, note: Note): Result<Boolean>

    suspend fun postAvgMood(
        uid: String,
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean>

    suspend fun postPsyTest(uid: String, psyTest: PsyTest): Result<Boolean>

    suspend fun uploadNoteImage(uid: String, noteImage: Bitmap, date: String): Result<String>

    suspend fun updateNote(uid: String, editedNote: Note, noteId: String): Result<Boolean>

    suspend fun deleteNote(uid: String, note: Note): Result<Boolean>

    suspend fun deleteAvgMood(uid: String, avgMoodId: String): Result<Boolean>

    suspend fun deletePsyTest(uid: String, psyTest: PsyTest): Result<Boolean>
}