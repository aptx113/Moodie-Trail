package com.danteyu.studio.moodietrail.recordmood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.databinding.FragmentRecordDetailBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/2/5.
 */
class RecordDetailFragment : Fragment() {

    private val viewModel by viewModels<RecordDetailViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecordDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigateToHome.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
                viewModel.onHomeNavigated()
            }
        })

        viewModel.backToRecordMood.observe(this, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.onRecordMoodBacked()
            }
        })

        return binding.root
    }
}