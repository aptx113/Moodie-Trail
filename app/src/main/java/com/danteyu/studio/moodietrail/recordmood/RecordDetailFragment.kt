package com.danteyu.studio.moodietrail.recordmood

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.MainActivity
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.DialogRecordDetailBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by George Yu on 2020/2/5.
 */
class RecordDetailFragment : AppCompatDialogFragment() {

    private val viewModel by viewModels<RecordDetailViewModel> {
        getVmFactory(
            RecordDetailFragmentArgs.fromBundle(
                arguments!!
            ).noteKey
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DialogRecordDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.editRecordDetailTag.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.addNoteTag()
                true
            } else false
        }

        binding.recyclerRecordDetailTags.adapter = TagAdapter(viewModel)

        viewModel.navigateToHome.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
                (activity as MainActivity).bottomNavView.selectedItemId = R.id.navigation_home
                viewModel.onHomeNavigated()
            }
        })

        viewModel.backToRecordMood.observe(this, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.onRecordMoodBacked()
            }
        })

        return binding.root
    }
}