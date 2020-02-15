package com.danteyu.studio.moodietrail.psytestrecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestRecordBinding

class PsyTestRecordFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPsyTestRecordBinding.inflate(inflater, container, false)

        return binding.root
    }
}