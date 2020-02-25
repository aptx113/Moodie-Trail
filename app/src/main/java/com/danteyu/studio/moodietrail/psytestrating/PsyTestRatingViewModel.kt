package com.danteyu.studio.moodietrail.psytestrating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger

/**
 * Created by George Yu on 2020/2/16.
 */

class PsyTestRatingViewModel(private val moodieTrailRepository: MoodieTrailRepository) :
    ViewModel() {

    private val _backToPsyTestResult = MutableLiveData<Boolean>()

    val backToPsyTestResult: LiveData<Boolean>
        get() = _backToPsyTestResult

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
    }

    fun backToPsyTestResult() {
        _backToPsyTestResult.value = true
    }

    fun onPsyTestResultBacked() {
        _backToPsyTestResult.value = null
    }
}