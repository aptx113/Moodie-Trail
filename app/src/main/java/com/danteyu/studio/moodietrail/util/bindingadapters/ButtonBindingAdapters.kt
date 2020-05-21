package com.danteyu.studio.moodietrail.util.bindingadapters

import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.model.Note
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Mood
import com.danteyu.studio.moodietrail.util.Util.getColor
import com.danteyu.studio.moodietrail.util.Util.getDrawable
import com.danteyu.studio.moodietrail.util.Util.getString

/**
 * Created by George Yu on 2020/3/5.
 */
@BindingAdapter("buttonMoodColor")
fun bindMoodColorForButton(button: Button, mood: Int?) {
    mood?.let {
        button.background = getDrawable(
            when (it) {
                Mood.VERY_BAD.value -> R.drawable.button_record_detail_ripple_very_bad
                Mood.BAD.value -> R.drawable.button_record_detail_ripple_bad
                Mood.NORMAL.value -> R.drawable.button_record_detail_ripple_normal
                Mood.GOOD.value -> R.drawable.button_record_detail_ripple_good
                Mood.VERY_GOOD.value -> R.drawable.button_record_detail_ripple_very_good
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