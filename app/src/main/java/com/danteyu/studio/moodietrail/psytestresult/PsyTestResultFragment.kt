package com.danteyu.studio.moodietrail.psytestresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.*
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestResultBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.psytestresult.PsyTestResultViewModel.Companion.DELETE_PSY_TEST_FAIL
import com.danteyu.studio.moodietrail.psytestresult.PsyTestResultViewModel.Companion.DELETE_PSY_TEST_SUCCESS
import com.danteyu.studio.moodietrail.util.Util.getDimensionPixelSize
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by George Yu on 2020/2/15.
 */
class PsyTestResultFragment : Fragment() {
    /**
     * Lazily initialize [PsyTestResultViewModel]
     */
    val viewModel by viewModels<PsyTestResultViewModel> {
        getVmFactory(
            PsyTestResultFragmentArgs.fromBundle(
                arguments!!
            ).psyTestKey
        )
    }
    private lateinit var binding: FragmentPsyTestResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when Fragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                // Handle the back button event
                override fun handleOnBackPressed() {
                    val mainViewModel by activityViewModels<MainViewModel> { getVmFactory() }
                    mainViewModel.navigateToPsyTestRecordByBottomNav()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPsyTestResultBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.buttonMentalHealthResourcePsyTestResult.setOnClickListener {
            activity.showToast("Coming Soon")
        }

        viewModel.psyTestEntries.observe(viewLifecycleOwner, Observer {
            it?.let {
                displayPsyTestData(it, binding.barChartItemsScorePsyTestResult)
            }
        })

        viewModel.showDeletePsyTestDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                showDeletePsyTestDialog(it)
                viewModel.onDeletePsyTestDialogShowed()
            }
        })

        viewModel.psyTestRelatedCondition.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    DELETE_PSY_TEST_SUCCESS -> activity.showToast(getString(R.string.delete_success))
                    DELETE_PSY_TEST_FAIL -> viewModel.error.value ?: getString(R.string.love_u_3000)
                    else -> {
                    }
                }
            }
        })

        viewModel.navigateToPsyTestRating.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(PsyTestResultFragmentDirections.navigateToPsyTestRatingFragment())
                viewModel.onPsyTestRatingNavigated()
            }
        })

        viewModel.navigateToPsyTestRecordByBottomNav.observe(viewLifecycleOwner, Observer {
            it?.let {
                (activity as MainActivity).bottomNavView.selectedItemId =
                    R.id.navigation_psy_test_record
                viewModel.onPsyTestRecordNavigated()
            }
        })

        viewModel.navigateToMentalHealthRes.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToMentalHealthResFragment())
                viewModel.onMentalHealthResNavigated()
            }
        })

        setupPsyTestChart(binding.barChartItemsScorePsyTestResult)

        return binding.root
    }

    private fun setupPsyTestChart(barChart: BarChart) {

        barChart.let {

            // disable description
            it.description.isEnabled = false

            it.setExtraOffsets(
                getDimensionPixelSize(R.dimen.offset_left_bar_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_top_bar_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_right_bar_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_bottom_bar_chart).toFloat()
            )

            // enable scaling and dragging
            it.isDragEnabled = false
            it.setScaleEnabled(false)
            it.setPinchZoom(true)

            // disable grid background
            it.setDrawGridBackground(false)

            // disable legend
            it.legend.isEnabled = false

            // disable data text
            it.setNoDataText("")
            it.animateY(ANIMATE_DURATION_MILLIS_BAR_CHART, Easing.EaseInCubic)

        }
        // set X axis
        val xAxis = barChart.xAxis
        xAxis.let {
            it.position = XAxis.XAxisPosition.BOTTOM
            it.setDrawGridLines(false)
            it.axisLineWidth = getDimensionPixelSize(R.dimen.width_bar_chart_axis_line).toFloat()
            it.textSize = getDimensionPixelSize(R.dimen.size_bar_chart_axis_text_size).toFloat()
            it.valueFormatter = BarChartXValueFormatter(viewModel.formatValue())
        }

        // set Y axis
        val yAxisLeft = barChart.axisLeft
        yAxisLeft.isEnabled = false

        val yAxisRight = barChart.axisRight
        yAxisRight.let {
            it.textSize = getDimensionPixelSize(R.dimen.size_text_value_chart).toFloat()
            it.axisLineWidth = getDimensionPixelSize(R.dimen.width_bar_chart_axis_line).toFloat()
            it.setDrawGridLines(false)
//            it.granularity = 1.0f
            it.isGranularityEnabled = true
        }
        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }

    private fun displayPsyTestData(psyTestEntries: List<BarEntry>, barChart: BarChart) {

        val barDataSet = BarDataSet(psyTestEntries, "")

        barDataSet.run {
            color = when (viewModel.psyTest.value!!.totalScore) {
                in 0.0..5.0 -> MoodieTrailApplication.instance.getColor(R.color.normal_range)
                in 6.0..9.0 -> MoodieTrailApplication.instance.getColor(R.color.light_range)
                in 10.0..14.0 -> MoodieTrailApplication.instance.getColor(R.color.medium_range)
                else -> MoodieTrailApplication.instance.getColor(R.color.heavy_range)
            }
            setDrawValues(false)
        }

        barChart.data = BarData(barDataSet)
        barChart.data.barWidth = PSY_TEST_RESULT_BAR_WIDTH
    }

    private fun showDeletePsyTestDialog(psyTest: PsyTest) {
        val builder = AlertDialog.Builder(this.context!!, R.style.AlertDialogTheme_Center)

        builder.setTitle(getString(R.string.check_delete_psy_test_message))
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setPositiveButton(getString(android.R.string.ok)) { _, _ ->
            UserManager.id?.let { viewModel.deletePsyTest(it, psyTest) }
        }.setNegativeButton(getString(R.string.text_cancel)) { _, _ ->
        }.show()
    }

    companion object {
        const val PSY_TEST_RESULT_BAR_WIDTH = 0.5f
        const val ANIMATE_DURATION_MILLIS_BAR_CHART = 1000
    }
}