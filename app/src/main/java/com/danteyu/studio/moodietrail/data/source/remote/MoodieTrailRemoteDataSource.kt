package com.danteyu.studio.moodietrail.data.source.remote

import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.*
import com.danteyu.studio.moodietrail.data.source.MoodieTrailDataSource
import com.danteyu.studio.moodietrail.util.Logger
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private const val KEY_CREATED_TIME = "createdTime"
    private const val KEY_WEEK = "weekOfMonth"
    private const val KEY_AVGMOOD = "avgMoodScore"
    private const val KEY_TIMELIST = "timeList"

    private val userReference = FirebaseFirestore.getInstance().collection(PATH_USERS)
    private fun getNotesRefFrom(uid: String): CollectionReference {
        return userReference.document(uid).collection(PATH_NOTES)
    }

    private fun getPsyTestsRefFrom(uid: String): CollectionReference {
        return userReference.document(uid).collection(PATH_PSYTESTS)
    }

    override suspend fun getNotes(uid: String): Result<List<Note>> =
        suspendCoroutine { continuation ->

            userReference.document(uid).collection(PATH_NOTES)
                .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
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
                .whereGreaterThanOrEqualTo(KEY_CREATED_TIME, startDate)
                .whereLessThanOrEqualTo(KEY_CREATED_TIME, endDate)
                .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
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
                .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
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
//
//                        val user = document.documents. .toObject(User::class.java)
//                        Logger.d(document.id + " => " + document.data)
////                        if(user == null)return@addOnCompleteListener
//
//                        continuation.resume(Result.Success(user!!))
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
