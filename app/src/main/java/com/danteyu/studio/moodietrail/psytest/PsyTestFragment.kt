package com.danteyu.studio.moodietrail.psytest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.setTouchDelegate

/**
 * Created by George Yu on 2020/2/14.
 */
class PsyTestFragment : Fragment() {

    /**
     * Lazily initialize [PsyTestViewModel]
     */
    val viewModel by viewModels<PsyTestViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPsyTestBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.buttonClosePsyTest.setTouchDelegate()

        viewModel.navigateToPsyTestBody.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(PsyTestFragmentDirections.navigateToPsyTestBodyFragment())
                viewModel.onPsyTestBodyNavigated()
            }
        })

        viewModel.leavePsyTest.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().popBackStack()
                viewModel.onPsyTestLeft()
            }
        })

        return binding.root
    }
}