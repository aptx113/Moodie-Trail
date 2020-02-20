package com.danteyu.studio.moodietrail.data.source.remote

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.*
import com.danteyu.studio.moodietrail.data.source.MoodieTrailDataSource
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getString
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by George Yu in Jan. 2020.
 *
 * Implementation of the Moodie Trail source that from network.
 */
object MoodieTrailRemoteDataSource : MoodieTrailDataSource {

    private const val PATH_USERS = "users"
    private const val PATH_NOTES = "notes"
    private const val PATH_PSYTESTS = "psyTests"
    private const val PATH_AVGMOODS = "avgMoods"
    private const val KEY_ID = "id"
    private const val KEY_DATE = "date"
    private const val KEY_WEEK = "weekOfMonth"
    private const val KEY_AVGMOOD = "avgMoodScore"
    private const val KEY_TIMELIST = "timeList"

    private val userReference = FirebaseFirestore.getInstance().collection(PATH_USERS)
    private val storageReference = FirebaseStorage.getInstance().reference

    private fun getNotesRefFrom(uid: String): CollectionReference {
        return userReference.document(uid).collection(PATH_NOTES)
    }

    private fun getPsyTestsRefFrom(uid: String): CollectionReference {
        return userReference.document(uid).collection(PATH_PSYTESTS)
    }

    override suspend fun getNotes(uid: String): Result<List<Note>> =
        suspendCoroutine { continuation ->

            userReference.document(uid).collection(PATH_NOTES)
                .orderBy(KEY_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Note>()
                        for (document in task.result!!) {
                            Logger.d(document.id + " => " + document.data)

                            val note = document.toObject(Note::class.java)
                            list.add(note)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getNotesByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<Note>> =
        suspendCoroutine { continuation ->

            getNotesRefFrom(uid)
                .whereGreaterThanOrEqualTo(KEY_DATE, startDate)
                .whereLessThanOrEqualTo(KEY_DATE, endDate)
                .orderBy(KEY_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Note>()
                        for (document in task.result!!) {
                            Logger.d(document.id + " => " + document.data)

                            val note = document.toObject(Note::class.java)
                            list.add(note)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getPsyTests(uid: String): Result<List<PsyTest>> =
        suspendCoroutine { continuation ->

            getPsyTestsRefFrom(uid)
                .orderBy(KEY_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<PsyTest>()
                        for (document in task.result!!) {
                            Logger.d(document.id + " => " + document.data)

                            val psyTest = document.toObject(PsyTest::class.java)
                            list.add(psyTest)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getUserProfile(id: String): Result<User> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance().collection(PATH_USERS).whereEqualTo(KEY_ID, id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val document = task.result!!
                        if (document.documents.size > 0) {
                            val user = document.documents.first().toObject(User::class.java)
                            continuation.resume(Result.Success(user!!))
                        } else {
                            continuation.resume(Result.Fail("No User Found"))
                        }

                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun signUpUser(user: User, id: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            val userData = FirebaseFirestore.getInstance().collection(PATH_USERS)
            val document = userData.document(id)

            user.id = document.id

            document
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("Post: $user")

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }


    override suspend fun postNote(uid: String, note: Note): Result<Boolean> =
        suspendCoroutine { continuation ->

            val document = userReference.document(uid).collection(PATH_NOTES).document()

            note.id = document.id

            document
                .set(note)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("Post: $note")

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun postAvgMood(
        uid: String,
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean> =
        suspendCoroutine { continuation ->
            val avgMoods = userReference.document(uid)
                .collection(PATH_AVGMOODS)
            val document = avgMoods.document(timeList)

            averageMood.idTime = document.id

            document
                .set(averageMood)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("Post: $averageMood")

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(
                            Result.Fail(
                                MoodieTrailApplication.instance.getString(
                                    R.string.you_know_nothing
                                )
                            )
                        )
                    }
                }
        }

    override suspend fun postPsyTest(uid: String, psyTest: PsyTest): Result<Boolean> =
        suspendCoroutine { continuation ->

            val document = userReference.document(uid).collection(PATH_PSYTESTS).document()

            psyTest.id = document.id

            document
                .set(psyTest)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("Post: $psyTest")

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun uploadNoteImage(
        uid: String,
        noteImage: Bitmap,
        date: String
    ): Result<String> = suspendCoroutine { continuation ->

        val imageRef = storageReference.child(
            MoodieTrailApplication.instance.getString(
                R.string.firebase_storage_reference,
                uid,
                date
            )
        )
        val uploadTask = imageRef.putFile(noteImage.toString().toUri())

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation imageRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Logger.i("downloadUri : $downloadUri")

                continuation.resume(Result.Success(downloadUri.toString()))
            } else {
                task.exception?.let {

                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(
                    Result.Fail(
                        MoodieTrailApplication.instance.getString(
                            R.string.you_know_nothing
                        )
                    )
                )
            }
        }
    }


    override suspend fun deleteNote(uid: String, note: Note): Result<Boolean> =
        suspendCoroutine { continuation ->

            userReference.document(uid).collection(PATH_NOTES)
                .document(note.id)
                .delete()
                .addOnSuccessListener {
                    Logger.i("Delete: $note")

                    continuation.resume(Result.Success(true))
                }.addOnFailureListener {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
        }
}
