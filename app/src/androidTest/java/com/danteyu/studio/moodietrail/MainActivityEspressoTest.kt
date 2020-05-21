package com.danteyu.studio.moodietrail

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.danteyu.studio.moodietrail.ui.consultationcall.ConsultationCallAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by George Yu on 2020/3/10.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testAddNewNoteFlow() {

        Thread.sleep(2500)
        onView(withId(R.id.fab)).perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.fab_record_mood)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.image_mood_very_good_record_mood)).perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.button_add_detail_record_mood)).perform(click())

        Thread.sleep(1500)

        onView(withId(R.id.edit_record_detail_content)).perform(
            replaceText("整合測試好好玩"),
            closeSoftKeyboard()
        )

        Thread.sleep(2000)

        onView(withId(R.id.edit_record_detail_tag)).perform(
            replaceText("espresso")
        )
        Thread.sleep(1500)

        onView(withId(R.id.button_record_detail_add_tags)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.button_record_detail_save)).perform(click())

        Thread.sleep(5000)


    }

    @Test
    fun testStartPsyTestFlow() {

        Thread.sleep(2000)
        onView(withId(R.id.fab)).perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.fab_start_test)).perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.button_start_psy_test)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.radio_insomnia_never)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.radio_anxiety_seldom)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.radio_angry_seldom)).perform(scrollTo()).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.radio_depression_seldom)).perform(scrollTo()).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.radio_inferiority_seldom)).perform(scrollTo()).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.radio_suicide_seldom)).perform(scrollTo()).perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.button_psy_text_body)).perform(scrollTo()).perform(click())

        Thread.sleep(3000)
        onView(withId(R.id.button_psy_test_result_complete)).perform(click())

        Thread.sleep(2500)

    }

    @Test
    fun testConsultationCallFlow() {

        Thread.sleep(2000)
        onView(withId(R.id.image_toolbar_call)).perform(click())

        Thread.sleep(1000)
        onView(withId(R.id.recycler_consultation_call)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ConsultationCallAdapter.ConsultationCallViewHolder>(
                2,
                click()
            )
        )

        Thread.sleep(1500)

    }
}