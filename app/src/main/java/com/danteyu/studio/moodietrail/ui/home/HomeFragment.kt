package com.danteyu.studio.moodietrail.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.data.model.Note
import com.danteyu.studio.moodietrail.databinding.FragmentHomeBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu in Jan. 2020.
 */
class HomeFragment : Fragment() {

    /**
     * Lazily initialize [HomeViewModel]
     */
    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerNote.adapter =
            HomeAdapter(HomeAdapter.OnClickListener { viewModel.navigateToRecordDetail(it) })

        binding.layoutSwipeRefreshNote.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refreshStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.layoutSwipeRefreshNote.isRefreshing = it
            }
        })

        viewModel.navigateToRecordDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navigateToRecordDetailFragment(it)
                )
                viewModel.onRecordDetailNavigated()
            }
        })

        viewModel.navigateToRecordMood.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.navigateToRecordMoodFragment(Note())
                )
                viewModel.onRecordMoodNavigated()
            }
        })

        return binding.root
    }
}
