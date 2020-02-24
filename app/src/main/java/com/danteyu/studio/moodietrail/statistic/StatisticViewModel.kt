package com.danteyu.studio.moodietrail.statistic

import android.graphics.Color
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ext.FORMAT_DD
import com.danteyu.studio.moodietrail.ext.FORMAT_YYYY_MM
import com.danteyu.studio.moodietrail.ext.FORMAT_YYYY_MM_DD
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [StatisticFragment].
 */
class StatisticViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _avgMoods = MutableLiveData<List<AverageMood>>()

    val avgMoods: LiveData<List<AverageMood>>
        get() = _avgMoods

    private val _showLineChartInfo = MutableLiveData<Boolean>()

    val showLineChartInfo: LiveData<Boolean>
        get() = _showLineChartInfo

    private val _currentDate = MutableLiveData<Long>()

    val currentDate: LiveData<Long>
        get() = _currentDate

    private val _avgMoodEntries = MutableLiveData<List<Entry>>()

    val avgMoodEntries: LiveData<List<Entry>>
        get() = _avgMoodEntries

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

    val calendar: Calendar = Calendar.getInstance()

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        initializeDate()
        getAvgMoods()
    }

    private fun initializeDate() {

        _currentDate.value = calendar.timeInMillis
    }

    private fun getThisMonthLastDate(): Int {

        calendar.timeInMillis = currentDate.value!!
        calendar.add(Calendar.MONTH, 0)
        calendar.set(
            Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Function to get Start Time Of Date in timestamp in milliseconds
     */
    private fun getStartDateOfMonth(timestamp: Long): Long? {

        val dayStart = Timestamp.valueOf(
            MoodieTrailApplication.instance.getString(
                R.string.timestamp_daybegin,
                "${timestamp.toDisplayFormat(FORMAT_YYYY_MM)}-01"
            )
        )
        Logger.i("ThisMonthFirstDate = ${timestamp.toDisplayFormat(FORMAT_YYYY_MM)}-01")
        return dayStart.time
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

    private fun getAvgMoods() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = UserManager.id?.let {
                moodieTrailRepository.getAvgMoodByDateRange(
                    it,
                    getStartDateOfMonth(_currentDate.value!!)!!,
                    getEndDateOfMonth(_currentDate.value!!)!!
                )
            }

            _avgMoods.value = when (result) {
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

            setEntriesForAvgMood(_avgMoods.value!!)
        }
    }

    private fun setEntriesForAvgMood(avgMoodData: List<AverageMood>) {

        val entries = ArrayList<Entry>().apply {
            avgMoodData.forEach { avgMood ->

                add(Entry(avgMood.time.toDisplayFormat(FORMAT_DD).toFloat(), avgMood.score))
                Logger.i("x = ${avgMood.time.toFloat()}, y = ${avgMood.score}")
            }
        }
        _avgMoodEntries.value = entries.reversed()
    }

    fun showLineChartInfo() {
        _showLineChartInfo.value = true
    }

    fun onLineChartInfoShowed() {
        _showLineChartInfo.value = null
    }

}