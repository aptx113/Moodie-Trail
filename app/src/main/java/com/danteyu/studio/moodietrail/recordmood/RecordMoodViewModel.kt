package com.danteyu.studio.moodietrail.recordmood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Created by George Yu on 2020/2/2.
 *
 * The [ViewModel] that is attached to the [RecordMoodFragment].
 */
class RecordMoodViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val note = MutableLiveData<Note>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Handle navigate to Home
    private val _navigateToHome = MutableLiveData<Boolean>()

    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    // Handle navigate to record detail
    private val _navigateToRecordDetail = MutableLiveData<Boolean>()

    val navigateToRecordDetail: LiveData<Boolean>
        get() = _navigateToRecordDetail

    // Handle leave Record Mood
    private val _leaveRecordMood = MutableLiveData<Boolean>()

    val leaveRecordMood: LiveData<Boolean>
        get() = _leaveRecordMood

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
    }

    fun navigateToHome() {
        _navigateToHome.value = true
    }

    fun onHomeNavigated() {
        _navigateToHome.value = null
    }

    fun navigateToRecordDetail() {

        val note = note.value

        _navigateToRecordDetail.value = true
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

}