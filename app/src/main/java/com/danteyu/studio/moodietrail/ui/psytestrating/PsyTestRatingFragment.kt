package com.danteyu.studio.moodietrail.ui.psytestrating


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestRatingBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/2/16.
 */
class PsyTestRatingFragment : Fragment() {

    /**
     * Lazily initialize [PsyTestRatingViewModel]
     */
    private val viewModel by viewModels<PsyTestRatingViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPsyTestRatingBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}
