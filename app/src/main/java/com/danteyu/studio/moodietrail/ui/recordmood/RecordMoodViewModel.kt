package com.danteyu.studio.moodietrail.ui.recordmood

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.model.AverageMood
import com.danteyu.studio.moodietrail.data.model.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.ui.login.UserManager
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Mood
import com.danteyu.studio.moodietrail.util.TimeFormat
import com.danteyu.studio.moodietrail.util.Util.getEndTimeOfDay
import com.danteyu.studio.moodietrail.util.Util.getStartTimeOfDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by George Yu on 2020/2/2.
 *
 * The [ViewModel] that is attached to the [RecordMoodFragment].
 */
class RecordMoodViewModel(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val arguments: Note
) : ViewModel() {

    // Detail has product data from arguments
    private val _note = MutableLiveData<Note>().apply {
        value = arguments
    }

    val note: LiveData<Note>
        get() = _note

    private val notesByDate = MutableLiveData<List<Note>>()

    val averageMoodScore: LiveData<Float> = Transformations.map(notesByDate) {
        var totalMoodScore = 0f
        var avgMoodScore = 0f

        if (it.count() > 0) {
            it.forEach { note ->
                note.mood.let {
                    totalMoodScore += note.mood
                }
            }

            avgMoodScore = totalMoodScore / it.count()
        }

        avgMoodScore
    }

    private val dateOfNote = MutableLiveData<Long>()

    private val weekOfMonthOfNote = MutableLiveData<Int>()

    val selectedMood = MutableLiveData<Int>()

    // Handle the error for write
    private val _invalidWrite = MutableLiveData<Int>()

    val invalidWrite: LiveData<Int>
        get() = _invalidWrite

    // Handle when write down is successful
    private val _writeDownSuccess = MutableLiveData<Boolean>()

    val writeDownSuccess: LiveData<Boolean>
        get() = _writeDownSuccess

    // Handle navigate to Home
    private val _navigateToHome = MutableLiveData<Boolean>()

    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    // Handle navigate to record detail
    private val _navigateToRecordDetail = MutableLiveData<Note>()

    val navigateToRecordDetail: LiveData<Note>
        get() = _navigateToRecordDetail

    // Handle leave Record Mood
    private val _leaveRecordMood = MutableLiveData<Boolean>()

    val leaveRecordMood: LiveData<Boolean>
        get() = _leaveRecordMood

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    val calendar: Calendar = Calendar.getInstance()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private val viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob].
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        initialDateOfNote()

    }

    private fun initialDateOfNote() {

        dateOfNote.value = when (_note.value?.date) {
            0L -> calendar.timeInMillis
            else -> _note.value?.date
        }

        weekOfMonthOfNote.value = when (_note.value?.weekOfMonth) {
            0 -> calendar.get(Calendar.WEEK_OF_MONTH)
            else -> _note.value?.weekOfMonth
        }
    }

    fun selectMood( mood: Mood) {

        when (mood) {

            Mood.VERY_BAD -> selectedMood.value = Mood.VERY_BAD.value
            Mood.BAD -> selectedMood.value = Mood.BAD.value
            Mood.NORMAL -> selectedMood.value = Mood.NORMAL.value
            Mood.GOOD -> selectedMood.value = Mood.GOOD.value
            Mood.VERY_GOOD -> selectedMood.value = Mood.VERY_GOOD.value
        }
    }

    fun prepareWriteDown(noteSavedType: NoteSavedType) {

        when (selectedMood.value) {
            0 -> _invalidWrite.value = INVALID_WRITE_MOOD_EMPTY
            null -> _invalidWrite.value = INVALID_WRITE_MOOD_EMPTY

            else -> when (noteSavedType) {
                NoteSavedType.DETAIL -> navigateToRecordDetail()
                NoteSavedType.QUICK -> writeDownQuickly()
            }
        }
    }

    private fun writeDownQuickly() {

        if (dateOfNote.value == null ||
            weekOfMonthOfNote.value == null || selectedMood.value == null
        ) return

        UserManager.id?.let {
            dateOfNote.value?.let { date ->
                weekOfMonthOfNote.value?.let { weekOfTheMonth ->
                    selectedMood.value?.let { mood ->
                        postNote(
                            it,
                            Note(
                                date = date,
                                weekOfMonth = weekOfTheMonth,
                                mood = mood
                            ), date
                        )
                    }
                }
            }
        }
    }

    private fun postNote(uid: String, note: Note, date: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.postNote(uid, note)

            _writeDownSuccess.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    getNotesResultByDateRange(
                        uid,
                        getStartTimeOfDay(date),
                        getEndTimeOfDay(date)
                    )
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    _invalidWrite.value = POST_NOTE_FAIL
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private fun getNotesResultByDateRange(uid: String, startDate: Long, endDate: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.getNotesByDateRange(uid, startDate, endDate)

            notesByDate.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            dateOfNote.value?.let { date ->
                averageMoodScore.value?.let {
                    postAvgMood(
                        uid,
                        AverageMood(
                            score = it,
                            time = getStartTimeOfDay(date)
                        ), date.toDisplayFormat(
                            TimeFormat.FORMAT_YYYY_MM_DD

                        )
                    )
                }
            }
        }
    }

    private fun postAvgMood(uid: String, averageMood: AverageMood, timeList: String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.postAvgMood(uid, averageMood, timeList)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    navigateToHome()

                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    _invalidWrite.value = POST_NOTE_FAIL
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    private fun navigateToRecordDetail() {

        dateOfNote.value?.let {
            weekOfMonthOfNote.value?.let { weekOfTheMonth ->
                selectedMood.value?.let { mood ->
                    val note = Note(
                        date = it,
                        weekOfMonth = weekOfTheMonth,
                        mood = mood
                    )

                    _navigateToRecordDetail.value = note
                }
            }
        }
    }

    fun onRecordDetailNavigated() {
        _navigateToRecordDetail.value = null
    }

    private fun navigateToHome() {
        _navigateToHome.value = true
    }

    fun onHomeNavigated() {
        _navigateToHome.value = null
    }

    fun leaveRecordMood() {
        _leaveRecordMood.value = true
    }

    fun onRecordMoodLeft() {
        _leaveRecordMood.value = null
    }

    companion object {
        const val INVALID_WRITE_MOOD_EMPTY = 0x11
        const val POST_NOTE_FAIL = 0x12
    }

}