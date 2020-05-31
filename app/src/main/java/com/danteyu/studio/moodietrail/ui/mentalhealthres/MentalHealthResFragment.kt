package com.danteyu.studio.moodietrail.ui.mentalhealthres

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.danteyu.studio.moodietrail.databinding.FragmentMentalHealthResBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/3/9.
 */
class MentalHealthResFragment : Fragment() {

    val viewModel by viewModels<MentalHealthResViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMentalHealthResBinding.inflate(
            inflater,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

}
