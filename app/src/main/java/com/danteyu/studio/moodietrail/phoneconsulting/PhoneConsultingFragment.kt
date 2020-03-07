package com.danteyu.studio.moodietrail.phoneconsulting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentPhoneConsultingBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/3/7.
 */
class PhoneConsultingFragment : Fragment() {

    val viewModel by viewModels<PhoneConsultingViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPhoneConsultingBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

}
