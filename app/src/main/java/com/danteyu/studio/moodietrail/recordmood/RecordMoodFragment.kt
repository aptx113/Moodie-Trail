package com.danteyu.studio.moodietrail.recordmood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentRecordMoodBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/2/2.
 */
class RecordMoodFragment : Fragment() {

    /**
     * Lazily initialize [RecordMoodViewModel]
     */
    val viewModel by viewModels<RecordMoodViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentRecordMoodBinding.inflate(inflater, container, false)
        binding.layoutRecordMood.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_scale_up
            )
        )


        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigateToHome.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
                viewModel.onHomeNavigated()
            }
        })

        viewModel.navigateToRecordDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToRecordDetailFragment())
                viewModel.onRecordDetailNavigated()
            }
        })

        viewModel.leaveRecordMood.observe(this, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
                viewModel.onRecordMoodLeft()
            }
        })

        return binding.root
    }
}