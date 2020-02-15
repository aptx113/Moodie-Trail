package com.danteyu.studio.moodietrail.psytest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger

/**
 * Created by George Yu on 2020/2/15.
 */
class PsyTestBodyViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _backToPsyTest = MutableLiveData<Boolean>()

    val backToPsyTest: LiveData<Boolean>
        get() = _backToPsyTest


    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

    }

    fun backToPsyTest() {
        _backToPsyTest.value = true
    }

    fun onPsyTestBacked() {
        _backToPsyTest.value = null
    }
}