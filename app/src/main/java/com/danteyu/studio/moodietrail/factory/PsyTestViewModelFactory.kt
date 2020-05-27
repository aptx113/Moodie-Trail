package com.danteyu.studio.moodietrail.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danteyu.studio.moodietrail.data.model.PsyTest
import com.danteyu.studio.moodietrail.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ui.psytestresult.PsyTestResultViewModel

/**
 * Created by George Yu on 2020/2/15.
 *
 * Factory for all ViewModels which need [PsyTest].
 */

@Suppress("UNCHECKED_CAST")
class PsyTestViewModelFactory(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val psyTest: PsyTest
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PsyTestResultViewModel::class.java) -> PsyTestResultViewModel(
                    moodieTrailRepository, psyTest
                )

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
