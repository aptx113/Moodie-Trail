package com.danteyu.studio.moodietrail.recordmood

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.MainActivity
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentRecordMoodBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.recordmood.RecordMoodViewModel.Companion.INVALID_WRITE_MOOD_EMPTY
import com.danteyu.studio.moodietrail.recordmood.RecordMoodViewModel.Companion.POST_NOTE_FAIL
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Created by George Yu on 2020/2/2.
 */
class RecordMoodFragment : Fragment() {

    /**
     * Lazily initialize [RecordMoodViewModel]
     */
    private val viewModel by viewModels<RecordMoodViewModel> {
        getVmFactory(
            RecordMoodFragmentArgs.fromBundle(
                arguments!!
            ).noteKey
        )
    }
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentRecordMoodBinding.inflate(inflater, container, false)
        binding.layoutRecordMood.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_scale_up
            )
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        calendar = viewModel.calendar

        viewModel.writeDownSuccess.observe(this, Observer {
            it?.let {
                when (it) {
                    true -> activity.showToast(getString(R.string.save_success))
                }
            }
        })

        viewModel.invalidWrite.observe(this, Observer {
            it?.let {
                when (it) {
                    INVALID_WRITE_MOOD_EMPTY -> {
                        activity.showToast(getString(R.string.select_a_mood))
                    }

                    POST_NOTE_FAIL -> {
                        activity.showToast(viewModel.error.value ?: getString(R.string.love_u_3000))
                    }
                    else -> {
                    }
                }
            }
        })

        viewModel.navigateToHome.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
                (activity as MainActivity).bottomNavView.selectedItemId = R.id.navigation_home
                viewModel.onHomeNavigated()
            }

        })

        viewModel.navigateToRecordDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToRecordDetailFragment(it))
                viewModel.onRecordDetailNavigated()
            }
        })

        viewModel.leaveRecordMood.observe(this, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
                viewModel.onRecordMoodLeft()
            }
        })

        viewModel.averageMoodScore.observe(this, Observer {
            Logger.w("averageMood = $it")
        })

        setupDatePickerDialog()
        setupTimePickerDialog()

        return binding.root
    }

    private fun setupDatePickerDialog() {

        val datePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                viewModel.updateDateAndTimeOfNote()
            }

        val datePickerDialog = DatePickerDialog(
            this.context!!,
            R.style.DatePicker,
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH).plus(1),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        viewModel.showDatePickerDialog.observe(this, Observer {
            it?.let {
                if (it) {

                    datePickerDialog.show()
                    viewModel.onDateDialogShowed()
                }
            }
        })
    }

    private fun setupTimePickerDialog() {

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                viewModel.updateDateAndTimeOfNote()
            }

        val timePickerDialog = TimePickerDialog(
            this.context,
            R.style.DatePicker,
            timePickerListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        viewModel.showTimePickerDialog.observe(this, Observer {
            it?.let {
                if (it) {

                    timePickerDialog.show()
                    viewModel.onTimeDialogShowed()
                }
            }
        })
    }
}