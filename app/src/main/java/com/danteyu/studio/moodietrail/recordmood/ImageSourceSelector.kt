package com.danteyu.studio.moodietrail.recordmood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.danteyu.studio.moodietrail.databinding.DialogImageSourceSelectorBinding

/**
 * Created by George Yu on 2020/2/20.
 */

class ImageSourceSelector(private val viewModel: RecordDetailViewModel) :
    AppCompatDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DialogImageSourceSelectorBinding.inflate(inflater, container, false)

        binding.buttonCamera.setOnClickListener { viewModel.launchCamera() }
        binding.buttonPhoto.setOnClickListener { viewModel.showGallery() }
        binding.buttonCancel.setOnClickListener { dismiss() }

        return binding.root
    }
}