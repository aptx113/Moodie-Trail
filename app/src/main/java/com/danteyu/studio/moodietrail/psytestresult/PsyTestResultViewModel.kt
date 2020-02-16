package com.danteyu.studio.moodietrail.psytestresult

import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository

/**
 * Created by George Yu on 2020/2/15.
 */
class PsyTestResultViewModel(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val argument: PsyTest
) : ViewModel() {}