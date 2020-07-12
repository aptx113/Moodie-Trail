package com.danteyu.studio.moodietrail.ui.mentalhealthres

import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Created by George Yu on 2020/3/9.
 */
class MentalHealthResViewModel(private val moodieTrailRepository: MoodieTrailRepository) :
    ViewModel() {

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

    }
}
