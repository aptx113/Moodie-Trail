package com.danteyu.studio.moodietrail.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danteyu.studio.moodietrail.MainViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.note.NoteViewModel
import com.danteyu.studio.moodietrail.profile.ProfileViewModel
import com.danteyu.studio.moodietrail.recordmood.RecordMoodDialog
import com.danteyu.studio.moodietrail.recordmood.RecordMoodViewModel
import com.danteyu.studio.moodietrail.statistic.StatisticViewModel
import com.danteyu.studio.moodietrail.testresult.TestResultViewModel
import java.lang.IllegalArgumentException


/**
 * Created by George Yu in Jan. 2020.
 *
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val moodieTrailRepository: MoodieTrailRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(moodieTrailRepository)

                isAssignableFrom(NoteViewModel::class.java) -> NoteViewModel(moodieTrailRepository)

                isAssignableFrom(StatisticViewModel::class.java) -> StatisticViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(TestResultViewModel::class.java) -> TestResultViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(RecordMoodViewModel::class.java) -> RecordMoodViewModel(
                    moodieTrailRepository
                )
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
