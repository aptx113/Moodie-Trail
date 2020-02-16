package com.danteyu.studio.moodietrail.psytestresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.MainActivity
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestResultBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import kotlinx.android.synthetic.main.activity_main.*

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

        viewModel.navigateToPsyTestRating.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(PsyTestResultFragmentDirections.navigateToPsyTestRatingFragment())
                viewModel.onPsyTestRatingNavigated()
            }
        })

        viewModel.navigateToPsyTestRecord.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToPsyTestRecordFragment())
                (activity as MainActivity).bottomNavView.selectedItemId =
                    R.id.navigation_psy_test_record
                viewModel.onPsyTestRecordNavigated()
            }
        })

        return binding.root
    }
}