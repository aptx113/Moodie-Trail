package com.danteyu.studio.moodietrail.consultationcall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.component.DividerItemDecoration
import com.danteyu.studio.moodietrail.databinding.FragmentConsultationCallBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.util.Util.getDrawable

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
            ConsultationCallAdapter()

        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(getDrawable(R.drawable.divider_consultation_call_recycler)!!)

        binding.recyclerConsultationCall.addItemDecoration(dividerItemDecoration)

        return binding.root
    }

}
