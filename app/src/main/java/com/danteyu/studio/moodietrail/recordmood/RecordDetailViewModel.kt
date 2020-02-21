package com.danteyu.studio.moodietrail.recordmood

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ext.FORMAT_YYYY_MM_DD
import com.danteyu.studio.moodietrail.ext.FORMAT_YYYY_MM_DD_HH_MM_SS
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
 * Created by George Yu on 2020/2/5.
 *
 * The [ViewModel] that is attached to the [RecordDetailDialog].
 */
class RecordDetailViewModel(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val argument: Note

) : ViewModel() {

    private val _note = MutableLiveData<Note>().apply { value = argument }

    val note: LiveData<Note>
        get() = _note

    private val _noteImage = MutableLiveData<String>()

    val noteImage: LiveData<String>
        get() = _noteImage

    private val notesByDate = MutableLiveData<List<Note>>()

    val averageMoodScore: LiveData<Float> = Transformations.map(notesByDate) {
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

//  MediatorLiveData to observe note's image. If noteImage's value change, then postNote
    val isUploadImageFinished = MediatorLiveData<Boolean>().apply {
        addSource(_noteImage) {
            UserManager.id?.let { id ->
                postNote(
                    id, Note(
                        date = _dateOfNote.value!!,
                        weekOfMonth = weekOFMonthOfNote.value!!,
                        mood = _note.value!!.mood,
                        image = _noteImage.value,
                        content = _note.value!!.content,
                        tags = tags.value
                    )
                )
            }
        }
    }

    val tags = MutableLiveData<MutableList<String>>().apply { value = mutableListOf() }

    //  Handle input tag
    val newTag = MutableLiveData<String>()

    private val _dateOfNote = MutableLiveData<Long>()

    val dateOfNote: LiveData<Long>
        get() = _dateOfNote

    val weekOFMonthOfNote = MutableLiveData<Int>()

    // Handle show DatePickerDialog
    private val _showDatePickerDialog = MutableLiveData<Boolean>()
    val showDatePickerDialog: LiveData<Boolean>
        get() = _showDatePickerDialog

    // Handle show TimePickerDialog
    private val _showTimePickerDialog = MutableLiveData<Boolean>()
    val showTimePickerDialog: LiveData<Boolean>
        get() = _showTimePickerDialog

    private val _selectedImage = MutableLiveData<Bitmap>()

    val selectedImage: LiveData<Bitmap>
        get() = _selectedImage

    //  Handle show ImageSourceSelectorDialog
    private val _showImageSelector = MutableLiveData<Boolean>()

    val showImageSelector: LiveData<Boolean>
        get() = _showImageSelector

    private val _launchCamera = MutableLiveData<Boolean>()

    val launchCamera: LiveData<Boolean>
        get() = _launchCamera

    private val _showGallery = MutableLiveData<Boolean>()

    val showGallery: LiveData<Boolean>
        get() = _showGallery

    // Handle when write down is successful
    private val _writeDetailSuccess = MutableLiveData<Boolean>()

    val writeDetailSuccess: LiveData<Boolean>
        get() = _writeDetailSuccess

    private val _invalidWrite = MutableLiveData<Int>()

    val invalidWrite: LiveData<Int>
        get() = _invalidWrite

    // Handle navigate to Home
    private val _navigateToHome = MutableLiveData<Boolean>()

    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    // Handle navigate to Record Mood
    private val _backToRecordMood = MutableLiveData<Boolean>()

    val backToRecordMood: LiveData<Boolean>
        get() = _backToRecordMood

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left =
                    MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_detail_tag)
            }
        }
    }

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

    /**
     * Function to get Start Time Of Date in timestamp in milliseconds
     */
    private fun getStartTimeOfDate(timestamp: Long): Long? {

        val dayStart = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_daybegin,
                timestamp.toDisplayFormat(FORMAT_YYYY_MM_DD)
            )
        )
        return dayStart.time
    }

    /**
     * Function to get End Time Of Date in timestamp in milliseconds
     */
    private fun getEndTimeOfDate(timestamp: Long): Long? {

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

    fun addNoteTag() {

        newTag.value?.let {
            tags.value?.add(it)

        }
        Logger.d("addNoteTags, tag = ${newTag.value}")
        tags.value = tags.value
        newTag.value = ""
        Logger.d("addNoteTags, tags = ${tags.value}")
    }

    fun removeNoteTag(tag: String) {

        tags.value?.let { tagList ->
            tagList.remove(tags.value?.findLast { it == tag })
        }
        tags.value = tags.value
    }

    fun setImage(bitmap: Bitmap?) {
        _selectedImage.value = bitmap
    }

    fun writeDetailWithImageOptional() {

        UserManager.id?.let {
            if (_selectedImage.value != null) {
                uploadNoteImage(
                    it, _selectedImage.value!!, _dateOfNote.value!!
                )
            } else {
                postNote(
                    it,
                    Note(
                        date = _dateOfNote.value!!,
                        weekOfMonth = weekOFMonthOfNote.value!!,
                        mood = _note.value!!.mood,
                        content = _note.value!!.content,
                        tags = tags.value
                    )
                )
            }
        }
    }

    private fun uploadNoteImage(uid: String, noteImage: Bitmap, date: Long) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.uploadNoteImage(
                uid, noteImage, date.toDisplayFormat(
                    FORMAT_YYYY_MM_DD_HH_MM_SS
                )
            )

            _noteImage.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    _invalidWrite.value = UPLOAD_IMAGE_FAIL
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

    private fun postNote(uid: String, note: Note) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.postNote(uid, note)

            _writeDetailSuccess.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    getNotesResultByDateRange(
                        uid,
                        getStartTimeOfDate(_dateOfNote.value!!)!!,
                        getEndTimeOfDate(_dateOfNote.value!!)!!
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
            postAvgMood(
                uid,
                AverageMood(
                    avgMoodScore = averageMoodScore.value!!,
                    time = getStartTimeOfDate(_dateOfNote.value!!)!!
                ), _dateOfNote.value?.toDisplayFormat(
                    FORMAT_YYYY_MM_DD
                )!!
            )
//            _refreshStatus.value = false
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
                    _invalidWrite.value = RecordMoodViewModel.POST_NOTE_FAIL
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

    fun showImageSelector() {
        _showImageSelector.value = true
    }

    fun onImageSelectorShowed() {
        _showImageSelector.value = null
    }

    fun launchCamera() {
        _launchCamera.value = true
    }

    fun onCameraLaunched() {
        _launchCamera.value = null
    }

    fun showGallery() {
        _showGallery.value = true
    }

    fun onGalleryShowed() {
        _showGallery.value = null
    }

    private fun navigateToHome() {
        _navigateToHome.value = true
    }

    fun onHomeNavigated() {
        _navigateToHome.value = null
    }

    fun backToRecordMood() {
        _backToRecordMood.value = true
    }

    fun onRecordMoodBacked() {
        _backToRecordMood.value = null
    }

    companion object {

        const val UPLOAD_IMAGE_FAIL = 0x31
        const val POST_NOTE_FAIL = 0x32

    }
}
