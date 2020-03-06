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

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
    }

}