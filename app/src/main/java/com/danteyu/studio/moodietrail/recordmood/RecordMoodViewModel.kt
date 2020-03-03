package com.danteyu.studio.moodietrail.recordmood

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Mood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ext.FORMAT_YYYY_MM_DD
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
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

    private val _notesByDate = MutableLiveData<List<Note>>()

    val notesByDate: LiveData<List<Note>>
        get() = _notesByDate

    val averageMoodScore: LiveData<Float> = Transformations.map(_notesByDate) {
        var totalMood = 0f
        var aveMood = 0f

        if (it.count() > 0) {
            it.forEach { note ->
                note.mood.let { mood ->
                    totalMood += note.mood
                }
            }

            aveMood = totalMood / it.count()
        }

        aveMood
    }

    private val _dateOfNote = MutableLiveData<Long>()

    val dateOfNote: LiveData<Long>
        get() = _dateOfNote

    val weekOFMonthOfNote = MutableLiveData<Int>()

    val selectedIcon = MutableLiveData<View>()

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
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
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

//    val date = MutableLiveData<Date>()

    /**
     * Function to get Start Time Of Day in timestamp in milliseconds
     */
    private fun getStartTimeOfDay(timestamp: Long): Long? {

        val dayStart = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_daybegin,
                timestamp.toDisplayFormat(FORMAT_YYYY_MM_DD)
            )
        )
        return dayStart.time
    }

    /**
     * Function to get End Time Of Day in timestamp in milliseconds
     */
    private fun getEndTimeOfDay(timestamp: Long): Long? {

        val dayEnd = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_dayend,
                timestamp.toDisplayFormat(FORMAT_YYYY_MM_DD)
            )
        )
        return dayEnd.time
    }

    private fun initialDateOfNote() {

        _dateOfNote.value = when (_note.value?.date) {
            0L -> calendar.timeInMillis
            else -> _note.value?.date
        }

        weekOFMonthOfNote.value = when (_note.value?.weekOfMonth) {
            0 -> calendar.get(Calendar.WEEK_OF_MONTH)
            else -> _note.value?.weekOfMonth
        }
    }

    fun updateDateAndTimeOfNote() {
        _dateOfNote.value = calendar.timeInMillis
        weekOFMonthOfNote.value = calendar.get(Calendar.WEEK_OF_MONTH)

    }

    fun selectMood(view: View, mood: Mood) {
        if (selectedIcon.value == view) return

        when (mood) {

            Mood.VERY_BAD -> selectedMood.value = 1
            Mood.BAD -> selectedMood.value = 2
            Mood.NORMAL -> selectedMood.value = 3
            Mood.GOOD -> selectedMood.value = 4
            Mood.VERY_GOOD -> selectedMood.value = 5
        }
//        selectedMood.value = mood
        selectedIcon.value?.isSelected = false
        selectedIcon.value = view
        selectedIcon.value?.isSelected = true
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
            weekOFMonthOfNote.value == null || selectedMood.value == null
        ) return

        UserManager.id?.let {
            postNote(
                it,
                Note(
                    date = _dateOfNote.value!!,
                    weekOfMonth = weekOFMonthOfNote.value!!,
                    mood = selectedMood.value!!
                )
            )
        }
    }

    private fun postNote(uid:String, note: Note) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.postNote(uid,note)

            _writeDownSuccess.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    getNotesResultByDateRange(
                        uid,
                        getStartTimeOfDay(_dateOfNote.value!!)!!,
                        getEndTimeOfDay(_dateOfNote.value!!)!!
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

    private fun getNotesResultByDateRange(uid: String,startDate: Long, endDate: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.getNotesByDateRange(uid, startDate, endDate)

            _notesByDate.value = when (result) {
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
            postAvgMood(uid,
                AverageMood(
                    score = averageMoodScore.value!!,
                    time = getStartTimeOfDay(_dateOfNote.value!!)!!
                ), _dateOfNote.value?.toDisplayFormat(
                    FORMAT_YYYY_MM_DD
                )!!
            )
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

        val note = Note(
            date = _dateOfNote.value!!,
            weekOfMonth = weekOFMonthOfNote.value!!,
            mood = selectedMood.value!!
        )

        _navigateToRecordDetail.value = note
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