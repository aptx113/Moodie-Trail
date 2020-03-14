package com.danteyu.studio.moodietrail

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.danteyu.studio.moodietrail.data.AverageMood
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.util.Util.getEndDateOfMonth
import com.danteyu.studio.moodietrail.util.Util.getEndTimeOfDay
import com.danteyu.studio.moodietrail.util.Util.getStartDateOfMonth
import com.danteyu.studio.moodietrail.util.Util.getStartTimeOfDay
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.danteyu.studio.moodietrail", appContext.packageName)
    }

    @Test
    fun whetherGetStartTimeOfDay_isCorrect() {

        val averageMood = AverageMood(
            idTime = "2020-03-08",
            score = 4.0f,
            time = 1583596800000
        )

        val result = getStartTimeOfDay(
            1583596800000
        )

        assertEquals(averageMood.time, result)

    }

    @Test
    fun whetherGetEndTimeOfDay_isCorrect() {

        val averageMood = AverageMood(
            idTime = "2020-03-08",
            score = 4.0f,
            time = 1583769599999
        )

        val result = getEndTimeOfDay(
            1583769599999
        )

        assertEquals(averageMood.time, result)

    }

    @Test
    fun whetherGetStartDateOfMonth_isCorrect() {

        val note = Note(
            date = 1582992000000
        )

        val result = getStartDateOfMonth(1582992000000)

        assertEquals(note.date, result)
    }

    @Test
    fun whetherGetEndDateOfMonth() {

        val note = Note(
            date = 1585670399999
        )

        val result = getEndDateOfMonth(Calendar.getInstance(), 1585670399999)

        assertEquals(note.date, result)
    }

    @Test
    fun averageMoodScore_isCorrect() {

        val notes = MutableLiveData<MutableList<Note>>().apply {
            value = mutableListOf()
        }

        notes.value?.apply {
            add(Note(mood = 2))
            add(Note(mood = 3))
            add(Note(mood = 4))
            add(Note(mood = 2))
        }

    }
}
