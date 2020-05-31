package com.danteyu.studio.moodietrail.ui.psytest


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestBodyBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestBodyViewModel.Companion.INVALID_FORMAT_ANGER_EMPTY
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestBodyViewModel.Companion.INVALID_FORMAT_ANXIETY_EMPTY
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestBodyViewModel.Companion.INVALID_FORMAT_DEPRESSION_EMPTY
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestBodyViewModel.Companion.INVALID_FORMAT_INFERIORITY_EMPTY
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestBodyViewModel.Companion.INVALID_FORMAT_INSOMNIA_EMPTY
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestBodyViewModel.Companion.INVALID_FORMAT_SUICIDE_EMPTY
import com.danteyu.studio.moodietrail.ui.psytest.PsyTestBodyViewModel.Companion.POST_PSY_TEST_FAIL

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

        viewModel.submitSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    activity.showToast(getString(R.string.psy_test_complete))
                }
            }
        })

        viewModel.invalidSubmit.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    INVALID_FORMAT_INSOMNIA_EMPTY -> {
                        activity.showToast(getString(R.string.do_not_forget_to_select_sleep))
                    }

                    INVALID_FORMAT_ANXIETY_EMPTY -> {
                        activity.showToast(getString(R.string.do_not_forget_to_select_anxiety))
                    }

                    INVALID_FORMAT_ANGER_EMPTY -> {
                        activity.showToast(getString(R.string.do_not_forget_to_select_anger))
                    }

                    INVALID_FORMAT_DEPRESSION_EMPTY -> {
                        activity.showToast(getString(R.string.do_not_forget_to_select_depression))
                    }

                    INVALID_FORMAT_INFERIORITY_EMPTY -> {
                        activity.showToast(getString(R.string.do_not_forget_to_select_depression))
                    }

                    INVALID_FORMAT_SUICIDE_EMPTY -> {
                        activity.showToast(getString(R.string.do_not_forget_to_select_suicide))
                    }

                    POST_PSY_TEST_FAIL -> {
                        activity.showToast(viewModel.error.value ?: getString(R.string.love_u_3000))
                    }
                    else -> {
                    }
                }
            }
        })

        viewModel.navigateToPsyTestResult.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    PsyTestBodyFragmentDirections.navigateToPsyTestResultFragment(
                        it
                    )
                )
                viewModel.onPsyTestResultNavigated()
            }
        })

        return binding.root
    }

}
