package com.danteyu.studio.moodietrail.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.danteyu.studio.moodietrail.databinding.FragmentStatisticBinding

class StatisticFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentStatisticBinding.inflate(inflater, container, false)

        return binding.root
    }
}