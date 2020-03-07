package com.danteyu.studio.moodietrail.phoneconsulting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.ConsultationCall
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by George Yu on 2020/3/7.
 *
 * The [ViewModel] that is attached to the [ConsultationCallFragment].
 */
class ConsultationCallViewModel(private val moodieTrailRepository: MoodieTrailRepository) :
    ViewModel() {

    private val _consultationCalls = MutableLiveData<List<ConsultationCall>>()

    val consultationCalls: LiveData<List<ConsultationCall>>
        get() = _consultationCalls

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

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

        getConsultationCalls()
    }

    private fun getConsultationCalls() {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.getConsultationCalls()

            _consultationCalls.value = when (result) {
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
                    _error.value = getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }

            }
        }
    }
}