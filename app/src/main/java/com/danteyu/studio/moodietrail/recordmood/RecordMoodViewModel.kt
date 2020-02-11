package com.danteyu.studio.moodietrail.recordmood

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
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

    private val _dateOfNote = MutableLiveData<Long>()

    val dateOfNote: LiveData<Long>
        get() = _dateOfNote

    val yearOfNote = MutableLiveData<Int>()

    val monthOfNote = MutableLiveData<Int>()

    val weekOFMonthOfNote = MutableLiveData<Int>()

    val dayOfNote = MutableLiveData<Int>()

    val hourOfNote = MutableLiveData<Int>()


    val selectedIcon = MutableLiveData<View>()

    val selectedMood = MutableLiveData<Int>()

    // Handle show DatePickerDialog
    private val _showDatePickerDialog = MutableLiveData<Boolean>()
    val showDatePickerDialog: LiveData<Boolean>
        get() = _showDatePickerDialog

    // Handle show TimePickerDialog
    private val _showTimePickerDialog = MutableLiveData<Boolean>()
    val showTimePickerDialog: LiveData<Boolean>
        get() = _showTimePickerDialog

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

    fun initialDateOfNote() {


        _dateOfNote.value = when (_note.value?.createdTime) {
            0L -> calendar.timeInMillis
            else -> _note.value?.createdTime

        }


        yearOfNote.value = when (_note.value?.year) {
            0 -> calendar.get(Calendar.YEAR)
            else -> _note.value?.year

        }

        monthOfNote.value = when (_note.value?.month) {
            0 -> calendar.get(Calendar.MONTH).plus(1)
            else -> _note.value?.month

        }

        weekOFMonthOfNote.value = when (_note.value?.weekOFMonth) {
            0 -> calendar.get(Calendar.WEEK_OF_MONTH)
            else -> _note.value?.weekOFMonth
        }

        dayOfNote.value = when (_note.value?.dayOfMonth) {
            0 -> calendar.get(Calendar.DAY_OF_MONTH)
            else -> _note.value?.dayOfMonth

        }

        hourOfNote.value = when (_note.value?.hour) {
            0 -> calendar.get(Calendar.HOUR_OF_DAY)
            else -> _note.value?.hour

        }

    }

    fun updateDateAndTimeOfNote() {
        _dateOfNote.value = calendar.timeInMillis
        yearOfNote.value = calendar.get(Calendar.YEAR)
        monthOfNote.value = calendar.get(Calendar.MONTH).plus(1)
        weekOFMonthOfNote.value = calendar.get(Calendar.WEEK_OF_MONTH)
        dayOfNote.value = calendar.get(Calendar.DAY_OF_MONTH)
        hourOfNote.value = calendar.get(Calendar.HOUR_OF_DAY)

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


        Logger.w("selectMood = $mood, selectIcon = ${view.id}")
    }

    fun prepareWriteDown(noteSavedType: NoteSavedType) {

        when (selectedMood.value) {
            0 -> _invalidWrite.value = INVALID_WRITE_MOOD_EMPTY
            null -> _invalidWrite.value = INVALID_WRITE_MOOD_EMPTY

            else -> when (noteSavedType) {
                NoteSavedType.DETAIL -> postThenNavigateToRecordDetail()
                NoteSavedType.QUICK -> writeDown()
            }
        }

    }

    private fun writeDown() {
        postNote(
            Note(
                createdTime = _dateOfNote.value!!,
                year = yearOfNote.value!!,
                month = monthOfNote.value!!,
                weekOFMonth = weekOFMonthOfNote.value!!,
                dayOfMonth = dayOfNote.value!!,
                hour = hourOfNote.value!!,
                mood = selectedMood.value!!
            )
        )
    }

    private fun postNote(note: Note) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.writeDownNote(note)

            _writeDownSuccess.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    navigateToHome(true)
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


    fun showDatePickerDialog() {
        _showDatePickerDialog.value = true
    }

    fun onDateDialogShowed() {
        _showDatePickerDialog.value = false
    }

    fun showTimePickerDialog() {
        _showTimePickerDialog.value = true
    }

    fun onTimeDialogShowed() {
        _showTimePickerDialog.value = false
    }

    fun navigateToHome(needRefresh: Boolean = false) {
        _navigateToHome.value = needRefresh
    }

    fun onHomeNavigated() {
        _navigateToHome.value = null
    }

    private fun postThenNavigateToRecordDetail() {

        val note = Note(
            createdTime = _dateOfNote.value!!,
            year = yearOfNote.value!!,
            month = monthOfNote.value!!,
            weekOFMonth = weekOFMonthOfNote.value!!,
            dayOfMonth = dayOfNote.value!!,
            hour = hourOfNote.value!!,
            mood = selectedMood.value!!
        )

        _navigateToRecordDetail.value = note
    }

    fun onRecordDetailNavigated() {
        _navigateToRecordDetail.value = null
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