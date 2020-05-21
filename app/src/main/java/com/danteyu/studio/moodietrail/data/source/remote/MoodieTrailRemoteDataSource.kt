package com.danteyu.studio.moodietrail.data.source.remote

import android.graphics.Bitmap
import android.net.Uri
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.*
import com.danteyu.studio.moodietrail.data.model.*
import com.danteyu.studio.moodietrail.data.source.MoodieTrailDataSource
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getAuth
import com.danteyu.studio.moodietrail.util.Util.getString
import com.danteyu.studio.moodietrail.util.Util.isInternetAvailable
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by George Yu in Jan. 2020.
 *
 * Implementation of the Moodie Trail source that from network.
 */
object MoodieTrailRemoteDataSource : MoodieTrailDataSource {

    private const val PATH_USERS = "users"
    private const val PATH_CONSULTATION_CALLS = "consultationCalls"
    private const val PATH_NOTES = "notes"
    private const val PATH_PSY_TESTS = "psyTests"
    private const val PATH_AVG_MOODS = "avgMoods"
    private const val KEY_ID = "id"
    private const val KEY_DATE = "date"
    private const val KEY_CONTENT = "content"
    private const val KEY_IMAGE = "image"
    private const val KEY_TAGS = "tags"
    private const val KEY_CREATED_TIME = "createdTime"
    private const val KEY_WEEK = "weekOfMonth"
    private const val KEY_TIME = "time"

    private val userReference = FirebaseFirestore.getInstance().collection(PATH_USERS)
    private val phoneConsultingInstReference = FirebaseFirestore.getInstance().collection(
        PATH_CONSULTATION_CALLS
    )
    private val storageReference = FirebaseStorage.getInstance().reference

    private fun getNotesRefFrom(uid: String): CollectionReference {
        return userReference.document(uid).collection(PATH_NOTES)
    }

    private fun getPsyTestsRefFrom(uid: String): CollectionReference {
        return userReference.document(uid).collection(PATH_PSY_TESTS)
    }

    private fun getAvgMoodRefFrom(uid: String): CollectionReference {
        return userReference.document(uid).collection(PATH_AVG_MOODS)
    }

    override suspend fun getNotesByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<Note>> =
        suspendCoroutine { continuation ->
            Logger.i("getNotesByDateRange = ${Thread.currentThread().name}")
            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                getNotesRefFrom(uid)
                    .whereGreaterThanOrEqualTo(KEY_DATE, startDate)
                    .whereLessThanOrEqualTo(KEY_DATE, endDate)
                    .orderBy(KEY_DATE, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener { task ->
                        Logger.i("getNotesByDateRange = ${Thread.currentThread()}")
                        if (task.isSuccessful) {
                            val list = mutableListOf<Note>()
                            for (document in task.result!!) {
                                Logger.d(document.id + " => " + document.data)

                                val note = document.toObject(Note::class.java)
                                list.add(note)
                            }
                            Logger.i("getNotesByDateRange = ${Thread.currentThread()}")
                            continuation.resume(Result.Success(list))
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
        }

    override suspend fun getPsyTests(uid: String): Result<List<PsyTest>> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

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
        }

    override suspend fun getAvgMoodByDateRange(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Result<List<AverageMood>> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                getAvgMoodRefFrom(uid)
                    .whereGreaterThanOrEqualTo(KEY_TIME, startDate)
                    .whereLessThanOrEqualTo(KEY_TIME, endDate)
                    .orderBy(KEY_TIME, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val list = mutableListOf<AverageMood>()
                            for (document in task.result!!) {
                                Logger.d(document.id + " => " + document.data)

                                val averageMood = document.toObject(AverageMood::class.java)
                                list.add(averageMood)
                            }
                            continuation.resume(Result.Success(list))
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
        }

    override suspend fun getConsultationCalls(): Result<List<ConsultationCall>> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                phoneConsultingInstReference
                    .orderBy(KEY_ID, Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val list = mutableListOf<ConsultationCall>()
                            for (document in task.result!!) {
                                Logger.d(document.id + " => " + document.data)

                                val phoneConsultingInst =
                                    document.toObject(ConsultationCall::class.java)
                                list.add(phoneConsultingInst)
                            }
                            continuation.resume(Result.Success(list))
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
        }

    override suspend fun getUserProfile(uid: String): Result<User> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                FirebaseFirestore.getInstance().collection(PATH_USERS).whereEqualTo(KEY_ID, uid)
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
        }

    override suspend fun signUpUser(user: User, id: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

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
        }

    override suspend fun handleFacebookAccessToken(token: AccessToken): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                Logger.d("handleFacebookAccessToken:${token.token}")

                val credential = FacebookAuthProvider.getCredential(token.token)
                getAuth().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information

                            continuation.resume(Result.Success(true))
                        } else {
                            task.exception?.let {

                                MoodieTrailApplication.instance.getString(R.string.login_fail_toast)
                                Logger.w("[${this::class.simpleName}] Authentication failed. signInWithCredential:failure: ${it.message}")
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
        }

    override suspend fun firebaseAuthWithGoogle(acct: GoogleSignInAccount): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                Logger.d("firebaseAuthWithGoogle:" + acct.id!!)

                val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
                getAuth().signInWithCredential(credential)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            Logger.d("signInWithCredential:success")

                            continuation.resume(Result.Success(true))
                        } else {
                            task.exception?.let {

                                MoodieTrailApplication.instance.getString(R.string.login_fail_toast)
                                Logger.w("[${this::class.simpleName}] signInWithCredential:failure: ${it.message} error_code =${it.message}")
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
        }

    override suspend fun postNote(uid: String, note: Note): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

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
        }

    override suspend fun postAvgMood(
        uid: String,
        averageMood: AverageMood,
        timeList: String
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                val avgMoods = userReference.document(uid)
                    .collection(PATH_AVG_MOODS)
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
        }

    override suspend fun postPsyTest(uid: String, psyTest: PsyTest): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                val document = userReference.document(uid).collection(PATH_PSY_TESTS).document()

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
        }

    override suspend fun uploadNoteImage(
        uid: String,
        noteImage: Bitmap,
        date: String
    ): Result<String> = suspendCoroutine { continuation ->

        if (!isInternetAvailable()) {
            continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
        } else {

            val imageRef = storageReference.child(
                MoodieTrailApplication.instance.getString(
                    R.string.firebase_storage_reference,
                    uid,
                    date
                )
            )

            val byteArrayOutput = ByteArrayOutputStream()

            noteImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutput)
            val bytes = byteArrayOutput.toByteArray()

            val uploadTask = imageRef.putBytes(bytes)

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
    }

    override suspend fun updateNote(
        uid: String,
        editedNote: Note,
        noteId: String
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                val document = getNotesRefFrom(uid).document(noteId)
                val editedNoteMap = hashMapOf(
                    KEY_DATE to editedNote.date,
                    KEY_WEEK to editedNote.weekOfMonth,
                    KEY_CONTENT to editedNote.content,
                    KEY_IMAGE to editedNote.image,
                    KEY_TAGS to editedNote.tags
                )

                document
                    .update(editedNoteMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Logger.i("Post: $editedNote")

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
        }


    override suspend fun deleteNote(uid: String, note: Note): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

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

    override suspend fun deleteAvgMood(uid: String, avgMoodId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                userReference.document(uid).collection(PATH_AVG_MOODS)
                    .document(avgMoodId)
                    .delete()
                    .addOnSuccessListener {
                        Logger.i("Delete: $avgMoodId")

                        continuation.resume(Result.Success(true))
                    }.addOnFailureListener {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
            }
        }

    override suspend fun deletePsyTest(uid: String, psyTest: PsyTest): Result<Boolean> =
        suspendCoroutine { continuation ->

            if (!isInternetAvailable()) {
                continuation.resume(Result.Fail(getString(R.string.internet_not_connected)))
            } else {

                userReference.document(uid).collection(PATH_PSY_TESTS)
                    .document(psyTest.id)
                    .delete()
                    .addOnSuccessListener {
                        Logger.i("Delete: $psyTest")

                        continuation.resume(Result.Success(true))
                    }.addOnFailureListener {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
            }
        }
}
