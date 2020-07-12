package com.danteyu.studio.moodietrail.ui.psytest

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.util.Util
import com.danteyu.studio.moodietrail.util.Util.getColor

/**
 * Created by George Yu on 2020/6/16.
 */

/**
 * Display partial text in BOLD and blue
 */
@BindingAdapter("boldPartialText", "startIndex", "endIndex")
fun bindSpanText(textView: TextView, text: String?, start: Int, end: Int) {
    text ?: return
    val a = text.run { SpannableString(this) }
    val b = a.run {
        a
        lambda@{ span: Any ->
            setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return@lambda a
        }
    }
    val c = b.let { setSpan ->
        setSpan(ForegroundColorSpan(getColor(R.color.blue_700_Dark)))
        setSpan(StyleSpan(Typeface.BOLD))
    }
    val d = c.let { textView.text = it }
//    text?.let {
//        val spannable = SpannableString(text)
//        spannable.setSpan(
//            ForegroundColorSpan(Util.getColor(R.color.blue_700_Dark)),
//            start,
//            end,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        spannable.setSpan(
//            StyleSpan(Typeface.BOLD),
//            start, end,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        textView.text = spannable
//    }
}