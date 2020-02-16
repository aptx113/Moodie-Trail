package com.danteyu.studio.moodietrail.psytestresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestResultBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/2/15.
 */

class PsyTestResultFragment : Fragment() {
    /**
     * Lazily initialize [PsyTestResultViewModel]
     */
    val viewModel by viewModels<PsyTestResultViewModel> {
        getVmFactory(
            PsyTestResultFragmentArgs.fromBundle(
                arguments!!
            ).psyTestKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPsyTestResultBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}