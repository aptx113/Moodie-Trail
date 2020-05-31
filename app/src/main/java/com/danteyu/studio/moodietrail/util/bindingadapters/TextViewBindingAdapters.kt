package com.danteyu.studio.moodietrail.util.bindingadapters

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.util.Mood
import com.danteyu.studio.moodietrail.util.TimeFormat
import com.danteyu.studio.moodietrail.util.Util.getColor
import com.danteyu.studio.moodietrail.util.Util.getString

/**
 * Created by George Yu on 2020/3/4.
 */
@BindingAdapter("psyResultRange")
fun bindPsyResultRangeScore(textView: TextView, totalScore: Float?) {
    totalScore?.let {
        textView.text = getString(
            when (it) {
                in 0.0..5.0 -> R.string.normal_range
                in 6.0..9.0 -> R.string.light_range
                in 10.0..14.0 -> R.string.medium_range
                else -> R.string.heavy_range
            }
        )
    }
}

@BindingAdapter("psyResultScoreRange")
fun bindPsyResultScoreRange(textView: TextView, totalScore: Float?) {
    totalScore?.let {
        textView.text = getString(
            when (it) {
                in 0.0..5.0 -> R.string.normal_score_range
                in 6.0..9.0 -> R.string.light_score_range
                in 10.0..14.0 -> R.string.medium_score_range
                else -> R.string.heavy_score_range
            }
        )
    }
}

@BindingAdapter("psyResultAdvice")
fun bindPsyResultAdvice(textView: TextView, totalScore: Float?) {
    totalScore?.let {
        textView.text = getString(
            when (it) {
                in 0.0..5.0 -> R.string.normal_result_advice
                in 6.0..9.0 -> R.string.light_result_advice
                in 10.0..14.0 -> R.string.medium_result_advice
                else -> R.string.heavy_result_advice
            }
        )
    }
}

@BindingAdapter("textToDisplayMoodColor")
fun bindMoodColorText(textView: TextView, mood: Int?) {
    mood?.let {
        textView.setTextColor(
            getColor(
                when (it) {
                    Mood.VERY_BAD.value -> R.color.mood_very_bad_Dark
                    Mood.BAD.value -> R.color.mood_bad_Dark
                    Mood.NORMAL.value -> R.color.mood_normal_Dark
                    Mood.GOOD.value -> R.color.mood_good_Dark
                    Mood.VERY_GOOD.value -> R.color.mood_very_good_Dark
                    else -> R.color.blue_700_Dark
                }
            )
        )
    }
}

@BindingAdapter("timeToDisplayFormat", "timeFormat")
fun bindDisplayFormatTime(textView: TextView, time: Long?, timeFormat: TimeFormat) {
    textView.text = time?.toDisplayFormat(timeFormat)
}

/**
 * Displays tag to [TextView] by prefix #
 */
@BindingAdapter("hashTag")
fun bindTag(textView: TextView, tag: String?) {
    tag.let {
        textView.text = MoodieTrailApplication.instance.getString(R.string.hash_tag, it)
    }
}

/**
 * Displays PsyTest Result score to [TextView] by [Float] with prefix
 */
@BindingAdapter("score")
fun bindPrefixScore(textView: TextView, score: Float?) {
    score?.let {
        val text =
            MoodieTrailApplication.instance.getString(R.string.psy_test_result_score, it.toInt())

        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.blue_700)),
            resultTotalScoreStartIndex,
            resultTotalScoreEndIndex,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            resultTotalScoreStartIndex, resultTotalScoreEndIndex,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(1.125f), resultTotalScoreStartIndex, resultTotalScoreEndIndex,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        textView.text = spannable
    }
}

/**
 * Displays PsyTest Result score to [TextView] by [Float] with suffix
 */
@BindingAdapter("scoreWithSuffix")
fun bindSuffixScore(textView: TextView, score: Float?) {
    score?.let {
        textView.text =
            MoodieTrailApplication.instance.getString(
                R.string.psy_text_result_score_only,
                it.toInt()
            )
    }
}

/**
 * Display partial text in BOLD and blue
 */
@BindingAdapter("boldPartialText", "startIndex", "endIndex")
fun bindSpanText(textView: TextView, text: String?, start: Int, end: Int) {
    text?.let {
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.blue_700_Dark)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
    }
}

/**
 * According to [message] to decide the visibility of [ProgressBar]
 */
@BindingAdapter("setupApiErrorMessage")
fun bindApiErrorMessage(view: TextView, message: String?) {
    when (message) {
        null, "" -> {
            view.visibility = View.GONE
        }
        else -> {
            view.text = message
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("serviceHour")
fun bindServiceHour(textView: TextView, serviceHour: String?) {
    serviceHour?.let {
        textView.text = MoodieTrailApplication.instance.getString(R.string.service_hour, it)
    }
}

const val resultTotalScoreStartIndex = 5
const val resultTotalScoreEndIndex = 7
