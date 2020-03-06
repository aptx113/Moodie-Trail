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
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.TimeFormat
import com.danteyu.studio.moodietrail.util.Util.getEndTimeOfDay
import com.danteyu.studio.moodietrail.util.Util.getStartTimeOfDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    private val noteImage = MutableLiveData<String>()

    private val notesByDate = MutableLiveData<List<Note>>()

    val averageMoodScore: LiveData<Float> = Transformations.map(notesByDate) {
        var totalMood = 0f
        var aveMood = 0f

        if (it.count() > 0) {
            it.forEach { note ->
                note.mood.let {
                    totalMood += note.mood
                }
            }
            aveMood = totalMood / it.count()
        }
        aveMood
    }

    val tags = MutableLiveData<MutableList<String>>().apply { value = mutableListOf() }

    //  Handle input tag
    val newTag = MutableLiveData<String>().apply {
        value = ""
    }

    private val _dateOfNote = MutableLiveData<Long>()

    val dateOfNote: LiveData<Long>
        get() = _dateOfNote

    private val weekOfMonthOfNote = MutableLiveData<Int>()

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

    private val _noteRelatedCondition = MutableLiveData<Int>()

    val noteRelatedCondition: LiveData<Int>
        get() = _noteRelatedCondition

    private val _showDeleteNoteDialog = MutableLiveData<Note>()
    val showDeleteNoteDialog: LiveData<Note>
        get() = _showDeleteNoteDialog

    // Handle navigate to Home
    private val _navigateToHome = MutableLiveData<Boolean>()

    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    // Handle navigate to Record Mood
    private val _backToRecordMood = MutableLiveData<Boolean>()

    val backToRecordMood: LiveData<Boolean>
        get() = _backToRecordMood

    private val _statusForPost = MutableLiveData<LoadApiStatus>()

    val statusForPost: LiveData<LoadApiStatus>
        get() = _statusForPost

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    //  MediatorLiveData to observe note's image. If noteImage's value change, then postNote
    val isUploadImageFinished = MediatorLiveData<Boolean>().apply {
        addSource(noteImage) {
            UserManager.id?.let { id ->
                _note.value?.let {
                    _dateOfNote.value?.let { date ->
                        weekOfMonthOfNote.value?.let { weekOfTheMonth ->
                            if (_note.value?.id == "") {
                                postNote(
                                    id, Note(
                                        date = date,
                                        weekOfMonth = weekOfTheMonth,
                                        mood = it.mood,
                                        image = noteImage.value,
                                        content = it.content,
                                        tags = tags.value
                                    ), date

                                )
                            } else {
                                updateNote(
                                    id, Note(
                                        date = date,
                                        weekOfMonth = weekOfTheMonth,
                                        image = noteImage.value,
                                        content = it.content,
                                        tags = tags.value
                                    ), it.id
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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

        initializeNote()
    }

    private fun initializeNote() {
        _note.value.let {
            it?.tags?.let { list ->
                tags.value = list
            }
        }
        initializeDateOfNote()
    }

    private fun initializeDateOfNote() {

        _dateOfNote.value = when (_note.value?.date) {
            0L -> calendar.timeInMillis
            else -> _note.value?.date
        }

        weekOfMonthOfNote.value = when (_note.value?.weekOfMonth) {
            0 -> calendar.get(Calendar.WEEK_OF_MONTH)
            else -> _note.value?.weekOfMonth
        }
    }

    fun updateDateOfNote() {
        _dateOfNote.value = calendar.timeInMillis
        weekOfMonthOfNote.value = calendar.get(Calendar.WEEK_OF_MONTH)
    }

    fun addNoteTag() {

        tags.value?.let {
            newTag.value?.let {
                tags.value?.add(it)
                tags.value = tags.value
                newTag.value = ""
                Logger.d("addNoteTags, tags = ${tags.value}")
            }
        }
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

    fun removeImage() {
        _selectedImage.value = null
        _note.value?.image = null
    }

    fun writeOrUpdate() {

        UserManager.id?.let {

            if (_note.value?.id == "") {
                writeDetailWithImageOptional()
            } else {
                updateDetailWithImageOptional()
            }
        }
    }

    private fun writeDetailWithImageOptional() {

        UserManager.id?.let {
            _note.value?.let { note ->
                _dateOfNote.value?.let { date ->
                    weekOfMonthOfNote.value?.let { weekOfTheMonth ->
                        if (_selectedImage.value != null) {
                            uploadNoteImage(
                                it, _selectedImage.value!!, date
                            )
                        } else {
                            postNote(
                                it,
                                Note(
                                    date = date,
                                    weekOfMonth = weekOfTheMonth,
                                    mood = note.mood,
                                    content = note.content,
                                    tags = tags.value
                                ), date
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateDetailWithImageOptional() {

        UserManager.id?.let {
            _note.value?.let { note ->
                _dateOfNote.value?.let { date ->
                    weekOfMonthOfNote.value?.let { weekOfTheMonth ->
                        if (_selectedImage.value != null) {
                            uploadNoteImage(
                                it, _selectedImage.value!!, date
                            )
                        } else {
                            updateNote(
                                it, Note(
                                    date = date,
                                    weekOfMonth = weekOfTheMonth,
                                    content = note.content,
                                    image = note.image,
                                    tags = tags.value
                                ), note.id
                            )
                        }
                    }
                }
            }
        }
    }

    private fun uploadNoteImage(uid: String, noteImage: Bitmap, date: Long) {

        coroutineScope.launch {

            _statusForPost.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.uploadNoteImage(
                uid, noteImage, date.toDisplayFormat(
                    TimeFormat.FORMAT_YYYY_MM_DD_HH_MM_SS
                )
            )

            this@RecordDetailViewModel.noteImage.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _statusForPost.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _statusForPost.value = LoadApiStatus.ERROR
                    _noteRelatedCondition.value = UPLOAD_IMAGE_FAIL
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _statusForPost.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _statusForPost.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private fun postNote(uid: String, note: Note, date: Long) {

        coroutineScope.launch {

            _statusForPost.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.postNote(uid, note)) {
                is Result.Success -> {
                    _error.value = null
                    _statusForPost.value = LoadApiStatus.DONE
                    getNotesResultByDateRange(
                        uid,
                        getStartTimeOfDay(date),
                        getEndTimeOfDay(date)
                    )
                    _noteRelatedCondition.value = POST_NOTE_SUCCESS

                }
                is Result.Fail -> {
                    _error.value = result.error
                    _statusForPost.value = LoadApiStatus.ERROR
                    _noteRelatedCondition.value = POST_NOTE_FAIL
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _statusForPost.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _statusForPost.value = LoadApiStatus.ERROR
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
            _dateOfNote.value?.let {
                if (averageMoodScore.value == 0f) {
                    deleteAvgMood(
                        uid, it.toDisplayFormat(
                            TimeFormat.FORMAT_YYYY_MM_DD
                        )
                    )

                } else {
                    averageMoodScore.value?.let { averageScore ->
                        postAvgMood(
                            uid,
                            AverageMood(
                                score = averageScore,
                                time = getStartTimeOfDay(it)
                            ), it.toDisplayFormat(
                                TimeFormat.FORMAT_YYYY_MM_DD
                            )
                        )
                    }
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
                    _noteRelatedCondition.value = RecordMoodViewModel.POST_NOTE_FAIL
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

    private fun updateNote(uid: String, editedNote: Note, noteId: String) {

        coroutineScope.launch {

            _statusForPost.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.updateNote(uid, editedNote, noteId)) {
                is Result.Success -> {
                    _error.value = null
                    _statusForPost.value = LoadApiStatus.DONE
                    _noteRelatedCondition.value = UPDATE_NOTE_SUCCESS

                }
                is Result.Fail -> {
                    _error.value = result.error
                    _statusForPost.value = LoadApiStatus.ERROR
                    _noteRelatedCondition.value = UPDATE_NOTE_FAIL
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _statusForPost.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _statusForPost.value = LoadApiStatus.ERROR
                }
            }
            navigateToHome()
        }
    }

    fun deleteNote(uid: String, note: Note) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.deleteNote(uid, note)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    getNotesResultByDateRange(
                        uid,
                        getStartTimeOfDay(note.date),
                        getEndTimeOfDay(note.date)
                    )
                    _noteRelatedCondition.value = DELETE_NOTE_SUCCESS

                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    _noteRelatedCondition.value = DELETE_NOTE_FAIL
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

    private fun deleteAvgMood(uid: String, averageMoodId: String) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.deleteAvgMood(uid, averageMoodId)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    navigateToHome()

                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
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

    fun showDeleteNoteDialog(note: Note) {
        _showDeleteNoteDialog.value = note
    }

    fun onDeleteNoteDialogShowed() {
        _showDeleteNoteDialog.value = null
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
        const val POST_NOTE_SUCCESS = 0x32
        const val POST_NOTE_FAIL = 0x33
        const val UPDATE_NOTE_SUCCESS = 0x34
        const val UPDATE_NOTE_FAIL = 0x35
        const val DELETE_NOTE_SUCCESS = 0x36
        const val DELETE_NOTE_FAIL = 0x37
    }
}
