package com.danteyu.studio.moodietrail.recordmood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository

/**
 * Created by George Yu on 2020/2/5.
 *
 * The [ViewModel] that is attached to the [RecordDetailFragment].
 */
class RecordDetailViewModel(
    private val moodieTrailRepository: MoodieTrailRepository

) : ViewModel() {

    val tags = MutableLiveData<MutableList<String>>().apply { value = mutableListOf() }

    val tag = MutableLiveData<String>()


    // Handle navigate to Home
    private val _navigateToHome = MutableLiveData<Boolean>()

    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    // Handle navigate to Record Mood
    private val _backToRecordMood = MutableLiveData<Boolean>()

    val backToRecordMood: LiveData<Boolean>
        get() = _backToRecordMood

    fun navigateToHome() {
        _navigateToHome.value = true
    }

    fun onHomeNavigated() {
        _navigateToHome.value = null
    }

    fun backToRecordMood() {
        _backToRecordMood.value = true
    }

    fun onRecordMoodBacked() {
        _backToRecordMood.value = null
    }
}