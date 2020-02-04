package com.danteyu.studio.moodietrail.recordmood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Created by George Yu on 2020/2/2.
 */

class RecordMoodViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    //    Handle leave Record Mood
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

    fun leaveRecordMood() {
        _leaveRecordMood.value = true
    }

}