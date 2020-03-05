package com.danteyu.studio.moodietrail.psytestrecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestRecordBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

class PsyTestRecordFragment : Fragment() {
    /**
     * Lazily initialize [PsyTestRecordViewModel]
     */
    private val viewModel by viewModels<PsyTestRecordViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPsyTestRecordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerPsyTestPsyTestRecord.adapter =
            PsyTestAdapter(PsyTestAdapter.OnClickListener { viewModel.navigateToPsyTestResult(it) })

        binding.layoutSwipeRefreshPsyTestRecord.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refreshStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.layoutSwipeRefreshPsyTestRecord.isRefreshing = it
            }
        })

        viewModel.navigateToPsyTestResult.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    PsyTestRecordFragmentDirections.actionPsyTestRecordFragmentToPsyTestResultFragment(
                        it
                    )
                )
                viewModel.onPsyTestResultNavigated()
            }
        })

        // Handle when user has no Psy Test record, the user can start Test by click hint on this
        viewModel.navigateToPsyTest.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navigateToPsyTestFragment()
                )
                viewModel.onPsyTestNavigated()
            }
        })

        return binding.root
    }
}