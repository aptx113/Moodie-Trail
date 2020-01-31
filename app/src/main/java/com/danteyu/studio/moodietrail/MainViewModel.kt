package com.danteyu.studio.moodietrail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.CurrentFragmentType


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

    //Handle Fab open and close
    fun onFabPressed() {
        _isFabOpen.value = !(_isFabOpen.value ?: false)
    }


}