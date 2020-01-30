package com.danteyu.studio.moodietrail.ext

import androidx.fragment.app.Fragment
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.factory.ViewModelFactory


/**
 * Created by George Yu in Jan. 2020.
 *
 * Extension functions for Fragment.
 */
fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MoodieTrailApplication).moodieTrailRepository
    return ViewModelFactory(repository)
}