package com.danteyu.studio.moodietrail.home

import android.graphics.Rect
import android.icu.util.Calendar
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.component.GridSpacingItemDecoration
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ext.FORMAT_YYYY_MM
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getStartDateOfMonth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [HomeFragment].
 */
class HomeViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()

    val notes: LiveData<List<Note>>
        get() = _notes

    private val _currentMonth = MutableLiveData<Long>()

    val currentMonth: LiveData<Long>
        get() = _currentMonth

    private val _navigateToRecordDetail = MutableLiveData<Note>()

    val navigateToRecordDetail: LiveData<Note>
        get() = _navigateToRecordDetail

    // Handle navigation to record mood
    private val _navigateToRecordMood = MutableLiveData<Boolean>()

    val navigateToRecordMood: LiveData<Boolean>
        get() = _navigateToRecordMood

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading notes
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val decoration = GridSpacingItemDecoration(
        2,
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.margin_basic), true
    )

    /**
     * When the [ViewModel] is finished, we can cancel our coroutine [viewModelJob], which tells the
     * service to stop
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val calendar = Calendar.getInstance()

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        initialDate()
        UserManager.id?.let {
            getNotesByDateRange(
                it,
                getStartDateOfMonth(_currentMonth.value!!)!!,
                getEndDateOfMonth(_currentMonth.value!!)!!
            )
        }
    }

    private fun initialDate() {

        _currentMonth.value = calendar.timeInMillis
    }

    private fun getThisMonthLastDate(): Int {

        calendar.timeInMillis = currentMonth.value!!
        calendar.add(java.util.Calendar.MONTH, 0)
        calendar.set(
            java.util.Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        )
        return calendar.get(java.util.Calendar.DAY_OF_MONTH)
    }

    /**
     * Function to get End Time Of Date in timestamp in milliseconds
     */
    private fun getEndDateOfMonth(timestamp: Long): Long? {

        val dayEnd = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_dayend,
                "${timestamp.toDisplayFormat(FORMAT_YYYY_MM)}-${getThisMonthLastDate()}"
            )
        )
        Logger.i("ThisMonthLastDate = ${timestamp.toDisplayFormat(FORMAT_YYYY_MM)}-${getThisMonthLastDate()}")
        return dayEnd.time
    }

    private fun getNotesByDateRange(uid: String, startDate: Long, endDate: Long) {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.getNotesByDateRange(uid, startDate, endDate)

            _notes.value = when (result) {
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
            _refreshStatus.value = false
        }
    }

    fun navigateToRecordDetail(note: Note) {
        _navigateToRecordDetail.value = note
    }

    fun onRecordDetailNavigated() {
        _navigateToRecordDetail.value = null
    }

    fun navigateToRecordMood() {
        _navigateToRecordMood.value = true
    }

    fun onRecordMoodNavigated() {
        _navigateToRecordMood.value = null
    }

    fun refresh() {
        if (_status.value != LoadApiStatus.LOADING)
            UserManager.id?.let {
                getNotesByDateRange(
                    it,
                    getStartDateOfMonth(_currentMonth.value!!)!!,
                    getEndDateOfMonth(_currentMonth.value!!)!!
                )
            }
    }
}