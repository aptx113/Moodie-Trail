package com.danteyu.studio.moodietrail.ui.psytestresult

import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.Event
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.model.PsyTest
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.PsyTestItem
import com.danteyu.studio.moodietrail.util.Util.getString
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.*

/**
 * Created by George Yu on 2020/2/15.
 */
class PsyTestResultViewModel(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val arguments: PsyTest
) : ViewModel() {

    // PsyTestResult has psyTest data from arguments
    private val _psyTest = MutableLiveData<PsyTest>().apply { value = arguments }

    val psyTest: LiveData<PsyTest>
        get() = _psyTest

    private val _psyTestEntries = MutableLiveData<List<BarEntry>>()

    val psyTestEntries: LiveData<List<BarEntry>>
        get() = _psyTestEntries

    private val _psyTestRelatedCondition = MutableLiveData<Int>()

    val psyTestRelatedCondition: LiveData<Int>
        get() = _psyTestRelatedCondition

    private val _showDeletePsyTestDialog = MutableLiveData<PsyTest>()

    val showDeletePsyTestDialog: LiveData<PsyTest>
        get() = _showDeletePsyTestDialog

    private val _navigateToPsyTestRating = MutableLiveData<Event<Boolean>>()

    val navigateToPsyTestRating: LiveData<Event<Boolean>>
        get() = _navigateToPsyTestRating

    private val _navigateToPsyTestRecordByBottomNav = MutableLiveData<Boolean>()

    val navigateToPsyTestRecordByBottomNav: LiveData<Boolean>
        get() = _navigateToPsyTestRecordByBottomNav

    private val _navigateToMentalHealthRes = MutableLiveData<Boolean>()

    val navigateToMentalHealthRes: LiveData<Boolean>
        get() = _navigateToMentalHealthRes

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

        setEntryForPsyTest()
    }

    private fun setEntryForPsyTest() {

        val psyTestEntries = ArrayList<BarEntry>().apply {
            _psyTest.value?.let {
                add(BarEntry(PsyTestItem.INSOMNIA.value.toFloat(), it.itemSleep))
                add(BarEntry(PsyTestItem.ANXIETY.value.toFloat(), it.itemAnxiety))
                add(BarEntry(PsyTestItem.ANGER.value.toFloat(), it.itemAnger))
                add(
                    BarEntry(
                        PsyTestItem.DEPRESSION.value.toFloat(),
                        it.itemDepression
                    )
                )
                add(
                    BarEntry(
                        PsyTestItem.INFERIORITY.value.toFloat(),
                        it.itemInferiority
                    )
                )
                add(BarEntry(PsyTestItem.SUICIDE.value.toFloat(), it.itemSuicide))
            }
        }
        _psyTestEntries.value = psyTestEntries
    }

    fun formatValue(): SparseArray<String> {

        val insomnia = getString(R.string.insomnia)
        val anxiety = getString(R.string.anxiety)
        val anger = getString(R.string.anger)
        val depression = getString(R.string.major_depression)
        val inferiority = getString(R.string.inferiority)
        val suicide = getString(R.string.suicide_thought)

        val itemLabels = SparseArray<String>()
        itemLabels.run {
            put(PsyTestItem.SUICIDE.value, suicide)
            put(PsyTestItem.INFERIORITY.value, inferiority)
            put(PsyTestItem.DEPRESSION.value, depression)
            put(PsyTestItem.ANGER.value, anger)
            put(PsyTestItem.ANXIETY.value, anxiety)
            put(PsyTestItem.INSOMNIA.value, insomnia)
        }
        return itemLabels
    }

    fun deletePsyTest(uid: String, psyTest: PsyTest) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.deletePsyTest(uid, psyTest)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE

                    _psyTestRelatedCondition.value = DELETE_PSY_TEST_SUCCESS
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    _psyTestRelatedCondition.value = DELETE_PSY_TEST_FAIL
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
            navigateToPsyTestRecordByBottomNav()
        }
    }

    fun showDeletePsyTestDialog(psyTest: PsyTest) {
        _showDeletePsyTestDialog.value = psyTest
    }

    fun onDeletePsyTestDialogShowed() {
        _showDeletePsyTestDialog.value = null
    }

    fun navigateToPsyTestRating() {
        _navigateToPsyTestRating.value = Event(true)
    }

    fun navigateToPsyTestRecordByBottomNav() {
        _navigateToPsyTestRecordByBottomNav.value = true
    }

    fun onPsyTestRecordNavigated() {
        _navigateToPsyTestRecordByBottomNav.value = null
    }

    fun navigateToMentalHealthRes() {
        _navigateToMentalHealthRes.value = true
    }

    fun onMentalHealthResNavigated() {
        _navigateToMentalHealthRes.value = null
    }

    companion object {

        const val DELETE_PSY_TEST_SUCCESS = 0x41
        const val DELETE_PSY_TEST_FAIL = 0x42
    }
}