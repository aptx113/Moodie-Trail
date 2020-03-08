package com.danteyu.studio.moodietrail.bindingadapters

import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.ConsultationCall
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.home.HomeAdapter
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.consultationcall.ConsultationCallAdapter
import com.danteyu.studio.moodietrail.psytestrecord.PsyTestAdapter
import com.danteyu.studio.moodietrail.recordmood.TagAdapter
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getDimensionPixelSize
import com.danteyu.studio.moodietrail.util.Util.getDrawable

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
    Logger.d("bindRecyclerViewWithTags, tags = $tags")
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

@BindingAdapter("consultationCall")
fun bindRecyclerViewWithConsultationCalls(
    recyclerView: RecyclerView,
    consultationCalls: List<ConsultationCall>?
) {
    consultationCalls?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ConsultationCallAdapter -> submitList(it)
            }
        }
    }
}

/**
 * Adds decoration to [RecyclerView]
 */
@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}

@BindingAdapter("itemPosition", "itemCount")
fun setupPaddingForGridItems(layout: ConstraintLayout, position: Int, count: Int) {

    val outsideHorizontal =
        getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)
    val insideHorizontal =
        getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)
    val outsideVertical =
        getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)
    val insideVertical =
        getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)

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

@BindingAdapter("layoutMoodColor")
fun bindMoodColorForLayout(constraintLayout: ConstraintLayout, mood: Int?) {
    mood?.let {
        constraintLayout.background = getDrawable(
            when (it) {
                1 -> R.color.mood_very_bad
                2 -> R.color.mood_bad
                3 -> R.color.mood_normal
                4 -> R.color.mood_good
                5 -> R.color.mood_very_good
                else -> R.color.blue_700
            }
        )
    }
}

/**
 * According to [LoadApiStatus] to decide the visibility of [ProgressBar]
 */
@BindingAdapter("setupApiStatus")
fun bindApiStatus(view: ProgressBar, status: LoadApiStatus?) {
    when (status) {
        LoadApiStatus.LOADING -> view.visibility = View.VISIBLE
        else -> view.visibility = View.GONE
    }
}

