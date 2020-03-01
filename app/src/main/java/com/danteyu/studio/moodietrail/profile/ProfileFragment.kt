package com.danteyu.studio.moodietrail.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.danteyu.studio.moodietrail.databinding.FragmentProfileBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast

class ProfileFragment : Fragment() {

    private val viewModel by viewModels<ProfileViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.layoutMentalResource.setOnClickListener {
            activity.showToast("Coming Soon")
        }

        binding.layoutNotification.setOnClickListener {
            activity.showToast("Coming Soon")
        }

        binding.layoutReport.setOnClickListener {
            activity.showToast("Coming Soon")
        }

        return binding.root
    }
}