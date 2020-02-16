package com.danteyu.studio.moodietrail.psytestresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger

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