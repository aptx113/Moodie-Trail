package com.danteyu.studio.moodietrail.ui.psytest.psyTestIHeader

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
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestFragmentDirections

/**
 * Created by George Yu on 2020/2/14.
 */
class PsyTestHeaderFragment : Fragment() {

    /**
     * Lazily initialize [PsyTestHeaderViewModel]
     */
    val viewModel by viewModels<PsyTestHeaderViewModel> { getVmFactory() }

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
