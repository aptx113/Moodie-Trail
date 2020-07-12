package com.danteyu.studio.moodietrail.ui.psytest.psyTestIHeader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger

/**
 * Created by George Yu on 2020/2/14.
 */
class PsyTestHeaderViewModel(moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _navigateToPsyTestBody = MutableLiveData<Boolean>()

    val navigateToPsyTestBody: LiveData<Boolean>
        get() = _navigateToPsyTestBody

    // Handle leave Record Mood
    private val _leavePsyTest = MutableLiveData<Boolean>()

    val leavePsyTest: LiveData<Boolean>
        get() = _leavePsyTest

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

    }

    fun navigateToPsyTestBody() {
        _navigateToPsyTestBody.value = true
    }

    fun onPsyTestBodyNavigated() {
        _navigateToPsyTestBody.value = null
    }

    fun leavePsyTest() {
        _leavePsyTest.value = true
    }

    fun onPsyTestLeft() {
        _leavePsyTest.value = null
    }

}
