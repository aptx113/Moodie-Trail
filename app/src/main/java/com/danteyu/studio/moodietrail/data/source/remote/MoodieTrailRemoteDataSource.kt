package com.danteyu.studio.moodietrail.data.source.remote

import android.icu.util.Calendar
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailDataSource
import com.danteyu.studio.moodietrail.util.Logger
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
    private const val PATH_TESTS = "tests"
    private const val KEY_CREATED_TIME = "createdTime"

    override suspend fun getNotes(): Result<List<Note>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_NOTES)
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
                    }
                    continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun writeDownNote(note: Note): Result<Boolean> = suspendCoroutine { continuation ->
        val notes = FirebaseFirestore.getInstance().collection(PATH_NOTES)
        val document = notes.document()

        note.id = document.id
        note.createdTime = Calendar.getInstance().timeInMillis

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
                    }
                    continuation.resume(Result.Fail(MoodieTrailApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun deleteNote(note: Note): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
