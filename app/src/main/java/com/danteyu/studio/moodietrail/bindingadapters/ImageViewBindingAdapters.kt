package com.danteyu.studio.moodietrail.bindingadapters

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.danteyu.studio.moodietrail.GlideApp
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.util.Mood
import com.danteyu.studio.moodietrail.util.PlaceHolder
import com.danteyu.studio.moodietrail.util.Util.getColor


/**
 * Created by George Yu on 2020/3/4.
 */
@BindingAdapter("moodImage")
fun ImageView.setMoodImage(item: Note?) {
    item?.let {
        setImageResource(
            when (item.mood) {
                Mood.VERY_BAD.value -> R.drawable.ic_mood_circle_very_bad_selected
                Mood.BAD.value -> R.drawable.ic_mood_circle_bad_selected
                Mood.NORMAL.value -> R.drawable.ic_mood_circle_normal_selected
                Mood.GOOD.value -> R.drawable.ic_mood_circle_good_selected
                Mood.VERY_GOOD.value -> R.drawable.ic_mood_circle_very_good_selected
                else -> R.drawable.ic_placeholder
            }
        )
    }
}

@BindingAdapter("moodImageDetail")
fun ImageView.setMoodImageDetail(item: Note?) {
    item?.let {
        setImageResource(
            when (item.mood) {
                Mood.VERY_BAD.value -> R.drawable.ic_mood_square_trans_very_bad
                Mood.BAD.value -> R.drawable.ic_mood_square_trans_bad
                Mood.NORMAL.value -> R.drawable.ic_mood_square_trans_normal
                Mood.GOOD.value -> R.drawable.ic_mood_square_trans_good
                Mood.VERY_GOOD.value -> R.drawable.ic_mood_square_trans_very_good
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

@BindingAdapter("placeholderMoodColor")
fun bindMoodColorForPlaceholder(imageView: ImageView, mood: Int?) {
    mood?.let {
        imageView.setBackgroundColor(
            getColor(
                when (it) {
                    Mood.VERY_BAD.value -> R.color.mood_very_bad_Light
                    Mood.BAD.value -> R.color.mood_bad_Light
                    Mood.NORMAL.value -> R.color.mood_normal_Light
                    Mood.GOOD.value -> R.color.mood_good_Light
                    Mood.VERY_GOOD.value -> R.color.mood_very_good_Light
                    else -> R.color.blue_700_Light
                }
            )
        )
    }
}

@BindingAdapter("imagePickerMoodColor")
fun bindMoodColorForImagePicker(imageView: ImageView, mood: Int?) {
    mood?.let {
        imageView.setColorFilter(
            getColor(
                when (it) {
                    Mood.VERY_BAD.value -> R.color.mood_very_bad_Light
                    Mood.BAD.value -> R.color.mood_bad_Light
                    Mood.NORMAL.value -> R.color.mood_normal_Light
                    Mood.GOOD.value -> R.color.mood_good_Light
                    Mood.VERY_GOOD.value -> R.color.mood_very_good_Light
                    else -> R.color.blue_700_Light
                }
            )
        )
    }
}

@BindingAdapter("srcMoodColor")
fun bindMoodColorForSrc(imageView: ImageView, mood: Int?) {
    mood?.let {
        imageView.setColorFilter(
            getColor(
                when (it) {
                    Mood.VERY_BAD.value -> R.color.mood_very_bad
                    Mood.BAD.value -> R.color.mood_bad
                    Mood.NORMAL.value -> R.color.mood_normal
                    Mood.GOOD.value -> R.color.mood_good
                    Mood.VERY_GOOD.value -> R.color.mood_very_good
                    else -> R.color.blue_700
                }
            )
        )
    }
}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().build()
        GlideApp.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
            )
            .into(imgView)
    }
}

@BindingAdapter("imageUrlByMood", "mood")
fun bindImageByMood(imgView: ImageView, imgUrl: String?, mood: Int?) {

    val imgUri = if (imgUrl == "null" || imgUrl == "" || imgUrl == null) {
        when (mood) {
            Mood.VERY_BAD.value -> PlaceHolder.VERY_BAD.value
            Mood.BAD.value -> PlaceHolder.BAD.value
            Mood.NORMAL.value -> PlaceHolder.NORMAL.value
            Mood.GOOD.value -> PlaceHolder.GOOD.value
            Mood.VERY_GOOD.value -> PlaceHolder.VERY_GOOD.value
            else -> ""
        }.toUri().buildUpon().scheme("https")
            .build()
    } else {
        imgUrl.toUri().buildUpon().scheme("https").build()
    }

    GlideApp.with(imgView.context)
        .load(imgUri)
        .transform(
            RoundedCorners(
                MoodieTrailApplication.instance.resources.getDimensionPixelSize(
                    R.dimen.margin_half
                )
            )
        )
        .apply(
            RequestOptions()
                .placeholder(R.drawable.ic_placeholder_record_detail)
                .error(R.mipmap.ic_launcher)
        )
        .into(imgView)
}