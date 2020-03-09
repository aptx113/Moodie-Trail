package com.danteyu.studio.moodietrail

import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.util.Util.getEndTimeOfDay

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun averageMoodScore_isCorrect() {

        val notes = mutableListOf<Note>().apply {
            add(Note(mood = 2))
            add(Note(mood = 3))
            add(Note(mood = 4))
            add(Note(mood = 2))
        }
        var totalMoodScore = 0f
        var avgMoodScore = 0f

        if (notes.count() > 0) {
            notes.forEach { note ->
                note.mood.let {
                    totalMoodScore += note.mood
                }
            }

            avgMoodScore = totalMoodScore / notes.count()
        }

        avgMoodScore

        assertEquals((2 + 3 + 4 + 2) / notes.count().toFloat(), avgMoodScore)

    }
}
