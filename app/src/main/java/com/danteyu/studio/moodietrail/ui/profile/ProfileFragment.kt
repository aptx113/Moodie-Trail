package com.danteyu.studio.moodietrail.ui.profile

import android.os.Bundle
import android.text.TextUtils
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

        binding.textProfileName.apply {
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSelected = true
        }

        binding.layoutMentalResourceProfile.setOnClickListener {
            activity.showToast("Coming Soon")
        }

        binding.layoutNotificationProfile.setOnClickListener {
            activity.showToast("Coming Soon")
        }

        binding.layoutReportProfile.setOnClickListener {
            activity.showToast("Coming Soon")
        }

        return binding.root
    }
}