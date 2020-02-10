package com.danteyu.studio.moodietrail.home

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [HomeFragment].
 */
class HomeViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()

    val notes: LiveData<List<Note>>
        get() = _notes

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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

        getNotesResult()

        Logger.w("calendar.time = ${calendar.time}")
        Logger.w("calendar.time.time = ${calendar.time.time}")
        Logger.w("calendar.timeINMillis = ${calendar.timeInMillis}")
        Logger.w("calendar.get(Calendar.YEAR) = ${calendar.get(Calendar.YEAR)}")
        Logger.w("calendar.get(Calendar.MONTH).plus(1) = ${calendar.get(Calendar.MONTH).plus(1)}")
        Logger.w("calendar.get(Calendar.DAY_OF_WEEK) = ${calendar.get(Calendar.DAY_OF_WEEK)}")
        Logger.w("calendar.get(Calendar.DAY_OF_MONTH) = ${calendar.get(Calendar.DAY_OF_MONTH)}")
        Logger.w("calendar.get(Calendar.DAY_OF_YEAR) = ${calendar.get(Calendar.DAY_OF_YEAR)}")
        Logger.w("calendar[Calendar.HOUR_OF_DAY] = ${calendar[Calendar.HOUR_OF_DAY]}")
        Logger.w("calendar.get(Calendar.HOUR_OF_DAY) = ${calendar.get(Calendar.HOUR_OF_DAY)}")
        Logger.w("calendar[Calendar.MINUTE] = ${calendar[Calendar.MINUTE]}")
        Logger.w("calendar[Calendar.SECOND] = ${calendar[Calendar.SECOND]}")
        Logger.w("calendar.set(Calendar.YEAR,1) = ${calendar.set(Calendar.YEAR, 1)}")
    }

    private fun getNotesResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.getNotes()

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

    fun refresh() {
        if (_status.value != LoadApiStatus.LOADING)
            getNotesResult()
    }
}