package com.danteyu.studio.moodietrail.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.danteyu.studio.moodietrail.MainViewModel
import com.danteyu.studio.moodietrail.data.Note
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

        binding.recyclerNote.adapter = HomeAdapter(HomeAdapter.OnClickListener {})

        binding.layoutSwipeRefreshNote.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refreshStatus.observe(this, Observer {
            it?.let {
                binding.layoutSwipeRefreshNote.isRefreshing = it
            }
        })

        ViewModelProvider(activity!!).get(MainViewModel::class.java).apply {
            refresh.observe(this@HomeFragment, Observer {
                it?.let {
                    viewModel.refresh()
                    onRefreshed()
                }
            })
        }

        return binding.root
    }
}