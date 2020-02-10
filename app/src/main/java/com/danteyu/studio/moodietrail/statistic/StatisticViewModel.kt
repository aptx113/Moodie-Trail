package com.danteyu.studio.moodietrail.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
import java.util.*


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [StatisticFragment].
 */
class StatisticViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()

    val notes: LiveData<List<Note>>
        get() = _notes


    val averageMood: LiveData<Float> = Transformations.map(_notes) {
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


    private val _currentDate = MutableLiveData<Date>()

    val currentDate: LiveData<Date>
        get() = _currentDate

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

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
        }
    }

}