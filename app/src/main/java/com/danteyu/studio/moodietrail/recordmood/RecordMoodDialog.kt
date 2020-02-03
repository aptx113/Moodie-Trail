package com.danteyu.studio.moodietrail.recordmood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.DialogRecordMoodBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/2/2.
 */
class RecordMoodDialog : AppCompatDialogFragment() {

    /**
     * Lazily initialize [RecordMoodViewModel]
     */
    val viewModel by viewModels<RecordMoodViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DialogRecordMoodBinding.inflate(inflater, container, false)
        binding.layoutRecordMood.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_scale_up))


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.leaveRecordMood.observe(this, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        return binding.root
    }
}