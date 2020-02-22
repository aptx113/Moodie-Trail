package com.danteyu.studio.moodietrail

import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Size
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.PlaceHolder
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.ext.*
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.home.HomeAdapter
import com.danteyu.studio.moodietrail.psytestrecord.PsyTestAdapter
import com.danteyu.studio.moodietrail.recordmood.TagAdapter
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getColor
import com.danteyu.studio.moodietrail.util.Util.getString
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.lang.Appendable

@BindingAdapter("notes")
fun bindRecyclerViewWithNotes(recyclerView: RecyclerView, notes: List<Note>?) {
    notes?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is HomeAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("tags")
fun bindRecyclerViewWithTags(recyclerView: RecyclerView, tags: List<String>?) {
    tags?.let {
        recyclerView.adapter?.apply {
            when (this) {

                is TagAdapter -> {
                    when (itemCount) {
                        0 -> {
                            submitList(it)
                            notifyDataSetChanged()
                        }
                        it.size -> notifyDataSetChanged()
                        else -> submitList(it)
                    }
                }
            }
        }
    }
    recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)
    Logger.d("bindRecyclerViewWithTags, taga = $tags")
}

@BindingAdapter("psyTests")
fun bindRecyclerViewWithPsyTests(recyclerView: RecyclerView, psyTests: List<PsyTest>?) {
    psyTests?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PsyTestAdapter -> submitList(it)
            }
        }
    }
}


@BindingAdapter("itemPosition", "itemCount")
fun setupPaddingForGridItems(layout: ConstraintLayout, position: Int, count: Int) {

    val outsideHorizontal =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)
    val insideHorizontal =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)
    val outsideVertical =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)
    val insideVertical =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)

    val layoutParams = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT,
        ConstraintLayout.LayoutParams.WRAP_CONTENT
    )

    when {
        position == 0 -> { // first item and confirm whether only 1 row
            layoutParams.setMargins(
                outsideHorizontal,
                outsideVertical,
                insideHorizontal,
                if (count > 2) insideVertical else outsideVertical
            )
        }
        position == 1 -> { // second item and confirm whether only 1 row
            layoutParams.setMargins(
                insideHorizontal,
                outsideVertical,
                outsideHorizontal,
                if (count > 2) insideVertical else outsideVertical
            )
        }
        count % 2 == 0 && position == count - 1 -> { // count more than 2 and item count is even
            layoutParams.setMargins(
                insideHorizontal,
                insideVertical,
                outsideHorizontal,
                outsideVertical
            )
        }
        (count % 2 == 1 && position == count - 1) || (count % 2 == 0 && position == count - 2) -> {
            layoutParams.setMargins(
                outsideHorizontal,
                insideVertical,
                insideHorizontal,
                outsideVertical
            )
        }
        position % 2 == 0 -> { // even
            when (position) {
                count - 1 -> layoutParams.setMargins(
                    insideHorizontal,
                    insideVertical,
                    outsideHorizontal,
                    outsideVertical
                ) // last 1
                count - 2 -> layoutParams.setMargins(
                    outsideHorizontal,
                    insideVertical,
                    insideHorizontal,
                    outsideVertical
                ) // last 2
                else -> layoutParams.setMargins(
                    outsideHorizontal,
                    insideVertical,
                    insideHorizontal,
                    insideVertical
                )
            }
        }
        position % 2 == 1 -> { // odd
            when (position) {
                count - 1 -> layoutParams.setMargins(
                    outsideHorizontal,
                    insideVertical,
                    insideHorizontal,
                    outsideVertical
                ) // last 1
                else -> layoutParams.setMargins(
                    insideHorizontal,
                    insideVertical,
                    outsideHorizontal,
                    insideVertical
                )
            }
        }
    }

    layout.layoutParams = layoutParams
}

@BindingAdapter("moodImage")
fun ImageView.setMoodImage(item: Note?) {
    item?.let {
        setImageResource(
            when (item.mood) {
                1 -> R.drawable.ic_mood_circle_very_bad_selected
                2 -> R.drawable.ic_mood_circle_bad_selected
                3 -> R.drawable.ic_mood_circle_normal_selected
                4 -> R.drawable.ic_mood_circle_good_selected
                5 -> R.drawable.ic_mood_circle_very_good_selected
                else -> R.drawable.ic_placeholder
            }
        )
    }
}

@BindingAdapter("moodDetailImage")
fun ImageView.setMoodIDetailmage(item: Note?) {
    item?.let {
        setImageResource(
            when (item.mood) {
                1 -> R.drawable.ic_mood_square_trans_very_bad
                2 -> R.drawable.ic_mood_square_trans_bad
                3 -> R.drawable.ic_mood_square_trans_normal
                4 -> R.drawable.ic_mood_square_trans_good
                5 -> R.drawable.ic_mood_square_trans_very_good
                else -> R.drawable.ic_placeholder
            }
        )
    }
}

@BindingAdapter("psyRatingImage")
fun ImageView.setPsyRatingImage(item: PsyTest?) {
    item?.let {
        setImageResource(
            when (item.totalScore) {
                in 0.0..5.0 -> R.drawable.ic_normal_range
                in 6.0..9.0 -> R.drawable.ic_light_range
                in 10.0..14.0 -> R.drawable.ic_medium_range
                else -> R.drawable.ic_heavy_range
            }
        )
    }
}

@BindingAdapter("psyRatingText")
fun bindPsyRatingText(textView: TextView, totalScore: Float?) {
    totalScore?.let {
        textView.text = MoodieTrailApplication.instance.getString(
            when (it) {
                in 0.0..5.0 -> R.string.normal_advice
                in 6.0..9.0 -> R.string.light_advice
                in 10.0..14.0 -> R.string.medium_advice
                else -> R.string.heavy_advice
            }
        )
    }
}

@BindingAdapter("psyRatingRangeText")
fun bindPsyRatingRangeText(textView: TextView, totalScore: Float?) {
    totalScore?.let {
        textView.text = MoodieTrailApplication.instance.getString(
            when (it) {
                in 0.0..5.0 -> R.string.normal_range
                in 6.0..9.0 -> R.string.light_range
                in 10.0..14.0 -> R.string.medium_range
                else -> R.string.heavy_range
            }
        )
    }
}

@BindingAdapter("psyRatingResultRangeText")
fun bindPsyRatingResultRangeText(textView: TextView, totalScore: Float?) {
    totalScore?.let {
        textView.text = MoodieTrailApplication.instance.getString(
            when (it) {
                in 0.0..5.0 -> R.string.normal_score
                in 6.0..9.0 -> R.string.light_score
                in 10.0..14.0 -> R.string.medium_score
                else -> R.string.heavy_score
            }
        )
    }
}

//General

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImageRadius(imgView: ImageView, imgUrl: String?) {

    val imgUri =
        if (imgUrl == "null" || imgUrl == "" || imgUrl == null) PlaceHolder.values().toList().shuffled().first().value.toUri().buildUpon().scheme(
            "https"
        ).build() else imgUrl.toUri().buildUpon().scheme("https").build()
    GlideApp.with(imgView.context)
        .load(imgUri)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
        )
        .into(imgView)

}

/**
 * Adds decoration to [RecyclerView]
 */
@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}

/**
 * Displays Date to [TextView] by [FORMAT_YYYY_MM_DD]
 */
@BindingAdapter("timeToDisplayDateFormat")
fun bindDisplayFormatDate(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat(FORMAT_YYYY_MM_DD)
}

/**
 * Displays Time to [TextView] by [FORMAT_HH_MM]
 */
@BindingAdapter("timeToDisplayTimeFormat")
fun bindDisplayFormatTime(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat(FORMAT_HH_MM)
}

/**
 * Displays Date to [TextView] by [FORMAT_YYYY_MM_DD_E]
 */
@BindingAdapter("timeToDisplayDateWithWeekFormat")
fun bindDisplayFormatMoodRecord(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat(FORMAT_YYYY_MM_DD_E)
}

/**
 * Displays Date to [TextView] by [FORMAT_YYYY_MM]
 */
@BindingAdapter("timeToDisplayDateYMFormat")
fun bindDisplayFormatForToolbar(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat(FORMAT_YYYY_MM)
}

/**
 * Displays Date to [TextView] by [FORMAT_YYYY_MM_DD_E_HH_MM]
 */
@BindingAdapter("timeToDisplayFormatForPsyRecord")
fun bindDisplayFormatForPsyRecord(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat(FORMAT_YYYY_MM_DD_E_HH_MM)
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
fun bindScorePrefix(textView: TextView, score: Float?) {
    score?.let {

        val text =
            MoodieTrailApplication.instance.getString(R.string.psy_test_result_score, it.toInt())
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.blue_700)),
            5,
            7,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(BOLD),
            5, 7,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(1.125f), 5, 7,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        textView.text = spannable
    }
}

/**
 * Displays PsyTest Result score to [TextView] by [Float] with suffix
 */
@BindingAdapter("scoreWithSuffix")
fun bindScoreSuffix(textView: TextView, score: Float?) {
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
fun bindTextSpan(textView: TextView, text: String?, start: Int, end: Int) {
    text?.let {
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.blue_700_Dark)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(BOLD),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
    }
}

/**
 * According to [LoadApiStatus] to decide the visibility of [ProgressBar]
 */
@BindingAdapter("setupApiStatus")
fun bindApiStatus(view: ProgressBar, status: LoadApiStatus?) {
    when (status) {
        LoadApiStatus.LOADING -> view.visibility = View.VISIBLE
        LoadApiStatus.DONE, LoadApiStatus.ERROR -> view.visibility = View.GONE
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

