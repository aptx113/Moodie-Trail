package com.danteyu.studio.moodietrail.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
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
import com.danteyu.studio.moodietrail.util.Mood
import com.danteyu.studio.moodietrail.util.TimeFormat
import com.danteyu.studio.moodietrail.util.Util.getEndDateOfMonth
import com.danteyu.studio.moodietrail.util.Util.getStartDateOfMonth
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [StatisticFragment].
 */
class StatisticViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _avgMoodScores = MutableLiveData<List<AverageMood>>()

    val avgMoodScores: LiveData<List<AverageMood>>
        get() = _avgMoodScores

    private val _notes = MutableLiveData<List<Note>>()

    val notes: LiveData<List<Note>>
        get() = _notes

    val veryBadCountInNotes: LiveData<Int>? = Transformations.map(notes) {
        it?.filter { note -> note.mood == Mood.VERY_BAD.value }?.size
    }

    val badCountInNotes: LiveData<Int>? = Transformations.map(notes) {
        it?.filter { note -> note.mood == Mood.BAD.value }?.size
    }

    val normalCountInNotes: LiveData<Int>? = Transformations.map(notes) {
        it?.filter { note -> note.mood == Mood.NORMAL.value }?.size
    }

    val goodCountInNotes: LiveData<Int>? = Transformations.map(notes) {
        it?.filter { note -> note.mood == Mood.GOOD.value }?.size
    }

    val veryGoodCountInNotes: LiveData<Int>? = Transformations.map(notes) {
        it?.filter { note -> note.mood == Mood.VERY_GOOD.value }?.size
    }

    private val _showLineChartInfo = MutableLiveData<Boolean>()

    val showLineChartInfo: LiveData<Boolean>
        get() = _showLineChartInfo

    private val _currentDate = MutableLiveData<Long>()

    val currentDate: LiveData<Long>
        get() = _currentDate

    private val _avgMoodScoreEntries = MutableLiveData<List<Entry>>()

    val avgMoodScoreEntries: LiveData<List<Entry>>
        get() = _avgMoodScoreEntries

    private val _noteEntries = MutableLiveData<List<PieEntry>>()

    val noteEntries: LiveData<List<PieEntry>>
        get() = _noteEntries

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

    private val calendar: Calendar = Calendar.getInstance()

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        initializeDate()
        getAvgMoodScores()
        getNotes()
    }

    private fun initializeDate() {

        _currentDate.value = calendar.timeInMillis
    }

    private fun getAvgMoodScores() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = UserManager.id?.let {
                _currentDate.value?.let { date ->
                    moodieTrailRepository.getAvgMoodScoresByDateRange(
                        it,
                        getStartDateOfMonth(date),
                        getEndDateOfMonth(calendar, date)
                    )
                }
            }

            _avgMoodScores.value = when (result) {
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
            _avgMoodScores.value?.let { setEntriesForAvgMoodScore(it) }
        }
    }

    private fun getNotes() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = UserManager.id?.let {
                _currentDate.value?.let { date ->
                    moodieTrailRepository.getNotesByDateRange(
                        it,
                        getStartDateOfMonth(date),
                        getEndDateOfMonth(calendar, date)
                    )
                }
            }

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
            _notes.value?.let { setEntriesForNote(it) }
        }
    }

    private fun setEntriesForAvgMoodScore(avgMoodData: List<AverageMood>) {

        val entries = ArrayList<Entry>().apply {
            avgMoodData.forEach { avgMood ->
                add(
                    Entry(
                        avgMood.time.toDisplayFormat(TimeFormat.FORMAT_DD).toFloat(),
                        avgMood.score
                    )
                )
                Logger.i("x = ${avgMood.time.toFloat()}, y = ${avgMood.score}")
            }
        }
        _avgMoodScoreEntries.value = entries.reversed()
    }

    private fun setEntriesForNote(noteData: List<Note>) {

        val entries = ArrayList<PieEntry>()

        val veryBadMoodNotes = noteData.filter { it.mood == Mood.VERY_BAD.value }
        val badMoodNotes = noteData.filter { it.mood == Mood.BAD.value }
        val normalNotes = noteData.filter { it.mood == Mood.NORMAL.value }
        val goodMoodNotes = noteData.filter { it.mood == Mood.GOOD.value }
        val veryGoodMoodNotes = noteData.filter { it.mood == Mood.VERY_GOOD.value }

        entries.apply {
            add(PieEntry(veryBadMoodNotes.size.toFloat()))
            add(PieEntry(badMoodNotes.size.toFloat()))
            add(PieEntry(normalNotes.size.toFloat()))
            add(PieEntry(goodMoodNotes.size.toFloat()))
            add(PieEntry(veryGoodMoodNotes.size.toFloat()))
        }
        _noteEntries.value = entries
    }

    fun showLineChartInfo() {
        _showLineChartInfo.value = true
    }

    fun onLineChartInfoShowed() {
        _showLineChartInfo.value = null
    }
}