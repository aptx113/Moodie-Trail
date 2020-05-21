package com.danteyu.studio.moodietrail.ext

import androidx.fragment.app.Fragment
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.data.model.Note
import com.danteyu.studio.moodietrail.data.model.PsyTest
import com.danteyu.studio.moodietrail.factory.NoteViewModelFactory
import com.danteyu.studio.moodietrail.factory.PsyTestViewModelFactory

import com.danteyu.studio.moodietrail.factory.ViewModelFactory


/**
 * Created by George Yu in Jan. 2020.
 *
 * Extension functions for Fragment.
 */
fun Fragment.getVmFactory(): ViewModelFactory {
    val repository =
        (requireContext().applicationContext as MoodieTrailApplication).moodieTrailRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(note: Note): NoteViewModelFactory {
    val repository =
        (requireContext().applicationContext as MoodieTrailApplication).moodieTrailRepository
    return NoteViewModelFactory(repository, note)
}

fun Fragment.getVmFactory(psyTest: PsyTest): PsyTestViewModelFactory {
    val repository =
        (requireContext().applicationContext as MoodieTrailApplication).moodieTrailRepository
    return PsyTestViewModelFactory(repository, psyTest)
}