package com.danteyu.studio.moodietrail.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel
import com.danteyu.studio.moodietrail.recordmood.RecordMoodViewModel

/**
 * Created by George Yu on 2020/2/5.
 *
 * Factory for all ViewModels which need [Note].
 */
@Suppress("UNCHECKED_CAST")
class NoteViewModelFactory(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val note: Note
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(RecordMoodViewModel::class.java) -> RecordMoodViewModel(
                    moodieTrailRepository, note
                )

                isAssignableFrom(RecordDetailViewModel::class.java) ->
                    RecordDetailViewModel(moodieTrailRepository, note)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
