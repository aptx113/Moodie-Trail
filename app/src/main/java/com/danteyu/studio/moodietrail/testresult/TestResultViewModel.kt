package com.danteyu.studio.moodietrail.testresult

import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [TestResultFragment].
 */
class TestResultViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {}