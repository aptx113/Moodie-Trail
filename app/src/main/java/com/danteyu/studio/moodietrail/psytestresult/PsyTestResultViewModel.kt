package com.danteyu.studio.moodietrail.psytestresult

import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getDrawable
import com.danteyu.studio.moodietrail.util.Util.getString
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.yield

/**
 * Created by George Yu on 2020/2/15.
 */
class PsyTestResultViewModel(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val arguments: PsyTest
) : ViewModel() {

    // PsyTestResult has psytest data from arguments
    private val _psyTest = MutableLiveData<PsyTest>().apply { value = arguments }

    val psyTest: LiveData<PsyTest>
        get() = _psyTest

    private val _psyTestEntries = MutableLiveData<List<BarEntry>>()
    val psyTestEntries: LiveData<List<BarEntry>>
        get() = _psyTestEntries

    private val _navigateToPsyTestRating = MutableLiveData<Boolean>()

    val navigateToPsyTestRating: LiveData<Boolean>
        get() = _navigateToPsyTestRating

    private val _navigateToPsyTestRecord = MutableLiveData<Boolean>()

    val navigateToPsyTestRecord: LiveData<Boolean>
        get() = _navigateToPsyTestRecord

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        setEntryForPsyTest()
    }

    fun setEntryForPsyTest() {
        val psyTestEntries = ArrayList<BarEntry>().apply {
            add(BarEntry(6f, _psyTest.value?.itemSleep!!))
            add(BarEntry(5f, _psyTest.value?.itemAnxiety!!))
            add(BarEntry(4f, _psyTest.value?.itemAnger!!))
            add(BarEntry(3f, _psyTest.value?.itemDepression!!))
            add(BarEntry(2f, _psyTest.value?.itemInferiority!!))
            add(BarEntry(1f, _psyTest.value?.itemSuicide!!))
        }

        _psyTestEntries.value = psyTestEntries

    }

//    fun formatYValue():SparseArray<String>{
//        val itemLabels = SparseArray<String>()
//        itemLabels.run {
//            put(0,"0")
//            put(1,"1")
//            put(2,"2")
//            put(3,"3")
//            put(4,"4")
//        }
//        return itemLabels
//    }

    fun formatValue(): SparseArray<String> {

        val insomnia = getString(R.string.insomnia)
        val anxiety = getString(R.string.anxiety)
        val anger = getString(R.string.anger)
        val depression = getString(R.string.major_depression)
        val inferiority = getString(R.string.inferiority)
        val suicide = getString(R.string.suicide_thought)

        val itemLabels = SparseArray<String>()
        itemLabels.run {
            put(1, suicide)
            put(2, inferiority)
            put(3, depression)
            put(4, anger)
            put(5, anxiety)
            put(6, insomnia)
        }
        return itemLabels
    }

    fun navigateToPsyTestRating() {
        _navigateToPsyTestRating.value = true
    }

    fun onPsyTestRatingNavigated() {
        _navigateToPsyTestRating.value = null
    }

    fun navigateToPsyTestRecord() {
        _navigateToPsyTestRecord.value = true
    }

    fun onPsyTestRecordNavigated() {
        _navigateToPsyTestRecord.value = null
    }
}