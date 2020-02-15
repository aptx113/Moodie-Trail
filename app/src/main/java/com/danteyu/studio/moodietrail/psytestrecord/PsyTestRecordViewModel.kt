package com.danteyu.studio.moodietrail.psytestrecord

import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [PsyTestRecordFragment].
 */
class PsyTestRecordViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {}