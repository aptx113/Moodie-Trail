package com.danteyu.studio.moodietrail.ui.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.DialogMoodLineChartInfoBinding

/**
 * Created by George Yu on 2020/2/24.
 */
class MoodLineChartInfoDialog : AppCompatDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Shadow)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DialogMoodLineChartInfoBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.dialog = this
        this.isCancelable = true

        return binding.root
    }

}