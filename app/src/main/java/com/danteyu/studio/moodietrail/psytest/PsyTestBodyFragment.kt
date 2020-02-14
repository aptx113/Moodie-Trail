package com.danteyu.studio.moodietrail.psytest


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestBodyBinding

/**
 * Created by George Yu on 2020/2/14.
 */
class PsyTestBodyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPsyTestBodyBinding.inflate(inflater)

        return binding.root
    }


}
