package com.danteyu.studio.moodietrail

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.note.NoteAdapter
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

@BindingAdapter("notes")
fun bindRecyclerView(recyclerView: RecyclerView, homeItems: List<Note>?) {
    homeItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is NoteAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("itemPosition", "itemCount")
fun setupPaddingForGridItems(layout: ConstraintLayout, position: Int, count: Int) {

    val outsideHorizontal =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_outside_horizontal_note_item)
    val insideHorizontal =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_horizontal_note_item)
    val outsideVertical =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_outside_vertical_note_item)
    val insideVertical =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_inside_vertical_note_item)

    val layoutParams = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.WRAP_CONTENT,
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

//General

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

@BindingAdapter("imageUrlRoundedCorners")
fun bindImageCircle(imgView: ImageView, imgUrl: String?) {

    val radius =
        MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.note_image_corner_radius)
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .transform(RoundedCornersTransformation(
                radius,
                0,
                RoundedCornersTransformation.CornerType.TOP
            ))
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(imgView)
    }
}