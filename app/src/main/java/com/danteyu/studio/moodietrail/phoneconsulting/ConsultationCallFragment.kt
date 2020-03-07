package com.danteyu.studio.moodietrail.phoneconsulting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.danteyu.studio.moodietrail.databinding.FragmentConsultationCallBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu on 2020/3/7.
 */
class ConsultationCallFragment : Fragment() {

    val viewModel by viewModels<ConsultationCallViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentConsultationCallBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerConsultationCall.adapter =
            ConsultationCallAdapter(ConsultationCallAdapter.OnClickListener {})

        return binding.root
    }

}
