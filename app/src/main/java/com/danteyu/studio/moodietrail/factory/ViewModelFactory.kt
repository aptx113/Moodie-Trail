package com.danteyu.studio.moodietrail.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danteyu.studio.moodietrail.MainViewModel
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.home.HomeViewModel
import com.danteyu.studio.moodietrail.login.LoginViewModel
import com.danteyu.studio.moodietrail.consultationcall.ConsultationCallViewModel
import com.danteyu.studio.moodietrail.mentalhealthres.MentalHealthResViewModel
import com.danteyu.studio.moodietrail.profile.ProfileViewModel
import com.danteyu.studio.moodietrail.psytest.PsyTestBodyViewModel
import com.danteyu.studio.moodietrail.psytest.PsyTestViewModel
import com.danteyu.studio.moodietrail.psytestrating.PsyTestRatingViewModel
import com.danteyu.studio.moodietrail.psytestrecord.PsyTestRecordViewModel
import com.danteyu.studio.moodietrail.statistic.StatisticViewModel


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

                isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(moodieTrailRepository)

                isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(moodieTrailRepository)

                isAssignableFrom(StatisticViewModel::class.java) -> StatisticViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(PsyTestRecordViewModel::class.java) -> PsyTestRecordViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(PsyTestViewModel::class.java) -> PsyTestViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(PsyTestBodyViewModel::class.java) -> PsyTestBodyViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(PsyTestRatingViewModel::class.java) -> PsyTestRatingViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(ConsultationCallViewModel::class.java) -> ConsultationCallViewModel(
                    moodieTrailRepository
                )

                isAssignableFrom(MentalHealthResViewModel::class.java) -> MentalHealthResViewModel(
                    moodieTrailRepository
                )

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
