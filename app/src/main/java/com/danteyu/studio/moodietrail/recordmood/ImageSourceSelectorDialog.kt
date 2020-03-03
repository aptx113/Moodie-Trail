package com.danteyu.studio.moodietrail.recordmood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.DialogImageSourceSelectorBinding

/**
 * Created by George Yu on 2020/2/20.
 */

class ImageSourceSelectorDialog(private val viewModel: RecordDetailViewModel) :
    AppCompatDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DialogImageSourceSelectorBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.dialog = this

        binding.buttonCameraImageSourceSelector.setOnClickListener { viewModel.launchCamera() }
        binding.buttonPhotoImageSourceSelector.setOnClickListener { viewModel.showGallery() }
        binding.buttonCancelImageSourceSelector.setOnClickListener { dismiss() }

        return binding.root
    }

}