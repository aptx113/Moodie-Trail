package com.danteyu.studio.moodietrail.psytest


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestBodyBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/2/14.
 */
class PsyTestBodyFragment : Fragment() {

    /**
     * Lazily initialize [PsyTestBodyViewModel]
     */
    val viewModel by viewModels<PsyTestBodyViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPsyTestBodyBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.navigateToPsyTestResult.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    PsyTestBodyFragmentDirections.navigateToPsyTestResultFragment(
                        PsyTest()
                    )
                )
                viewModel.onPsyTestResultNavigated()
            }
        })

        viewModel.backToPsyTest.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.onPsyTestBacked()
            }
        })

        return binding.root
    }


}
