package com.danteyu.studio.moodietrail.bindingadapters

import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Util.getString
import com.danteyu.studio.moodietrail.util.Util.getColor
import com.danteyu.studio.moodietrail.util.Util.getDrawable

/**
 * Created by George Yu on 2020/3/5.
 */
@BindingAdapter("buttonMoodColor")
fun bindMoodColorForButton(button: Button, mood: Int?) {
    mood?.let {
        button.background = getDrawable(
            when (it) {
                1 -> R.drawable.button_record_detail_ripple_very_bad
                2 -> R.drawable.button_record_detail_ripple_bad
                3 -> R.drawable.button_record_detail_ripple_normal
                4 -> R.drawable.button_record_detail_ripple_good
                5 -> R.drawable.button_record_detail_ripple_very_good
                else -> R.color.blue_700
            }
        )
    }
}

@BindingAdapter("imageButtonMoodColor", "newTag")
fun bindMoodColorForImageButton(imageButton: ImageButton, mood: Int?, newTag: String?) {
    mood?.let {
        if (newTag == "" || newTag == null) {
            imageButton.setColorFilter(getColor(R.color.gray_999999))
        } else {
            imageButton.setColorFilter(
                getColor(
                    when (it) {
                        1 -> R.color.mood_very_bad
                        2 -> R.color.mood_bad
                        3 -> R.color.mood_normal
                        4 -> R.color.mood_good
                        5 -> R.color.mood_very_good
                        else -> R.color.blue_700
                    }
                )
            )
        }
    }
}

/**
 * Determine text display on button base on [LoadApiStatus] and whether it is existed [Note]
 */
@BindingAdapter("loadApiStatus", "existOfNote")
fun bindTextForButton(button: Button, status: LoadApiStatus?, noteId: String?) {
    when (status) {

        LoadApiStatus.LOADING -> button.text = ""
        else ->
            when (noteId) {

                "" -> button.text = getString(R.string.record_detail_save)
                else -> button.text = getString(R.string.confirm_edit)
            }
    }
}