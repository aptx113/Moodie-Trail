package com.danteyu.studio.moodietrail.recordmood

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
import com.danteyu.studio.moodietrail.databinding.FragmentRecordMoodBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.setTouchDelegate
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.recordmood.RecordMoodViewModel.Companion.INVALID_WRITE_MOOD_EMPTY
import com.danteyu.studio.moodietrail.recordmood.RecordMoodViewModel.Companion.POST_NOTE_FAIL
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Created by George Yu on 2020/2/2.
 */
class RecordMoodFragment : Fragment() {

    /**
     * Lazily initialize [RecordMoodViewModel]
     */
    private val viewModel by viewModels<RecordMoodViewModel> {
        getVmFactory(
            RecordMoodFragmentArgs.fromBundle(
                arguments!!
            ).noteKey
        )
    }
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentRecordMoodBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.buttonRecordMoodClose.setTouchDelegate()

        calendar = viewModel.calendar

        viewModel.writeDownSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    true -> activity.showToast(getString(R.string.save_success))
                }
            }
        })

        viewModel.invalidWrite.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    INVALID_WRITE_MOOD_EMPTY -> {
                        activity.showToast(getString(R.string.select_a_mood))
                    }

                    POST_NOTE_FAIL -> {
                        activity.showToast(viewModel.error.value ?: getString(R.string.love_u_3000))
                    }
                    else -> {
                    }
                }
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
                (activity as MainActivity).bottomNavView.selectedItemId = R.id.navigation_home
                viewModel.onHomeNavigated()
            }

        })

        viewModel.navigateToRecordDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToRecordDetailFragment(it))
                viewModel.onRecordDetailNavigated()
            }
        })

        viewModel.leaveRecordMood.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
                viewModel.onRecordMoodLeft()
            }
        })

        viewModel.averageMoodScore.observe(viewLifecycleOwner, Observer {
            Logger.w("averageMood = $it")
        })

        return binding.root
    }
}