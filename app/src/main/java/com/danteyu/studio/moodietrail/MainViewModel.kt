package com.danteyu.studio.moodietrail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.CurrentFragmentType
import com.danteyu.studio.moodietrail.util.Logger


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [MainActivity].
 */
class MainViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    //Handle Fab open and close
    private val _isFabOpen = MutableLiveData<Boolean>()

    val isFabOpen: LiveData<Boolean>
        get() = _isFabOpen

    // Handle navigation to record mood
    private val _navigateToRecordMood = MutableLiveData<Boolean>()

    val navigateToRecordMood: LiveData<Boolean>
        get() = _navigateToRecordMood

    private val _refresh = MutableLiveData<Boolean>()

    val refresh: LiveData<Boolean>
        get() = _refresh

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
    }

    //Change Fab status when is pressed
    fun onFabPressed() {
        _isFabOpen.value = !(_isFabOpen.value ?: false)
    }

    fun navigateToRecordMood() {
        _navigateToRecordMood.value = true
        _isFabOpen.value = false
    }

    fun onRecordMoodNavigated() {
        _navigateToRecordMood.value = null
    }

    fun refresh() {
        _refresh.value = true
    }

    fun onRefreshed() {
        _refresh.value = null
    }

}