package com.danteyu.studio.moodietrail.psytestresult

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.MainActivity
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.databinding.FragmentPsyTestResultBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.setTouchDelegate
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.psytestresult.PsyTestResultViewModel.Companion.DELETE_PSY_TEST_FAIL
import com.danteyu.studio.moodietrail.psytestresult.PsyTestResultViewModel.Companion.DELETE_PSY_TEST_SUCCESS
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
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
    private lateinit var psyTestChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when Fragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            // Handle the back button event
            findNavController().navigate(NavigationDirections.navigateToPsyTestRecordFragment())
            (activity as MainActivity).bottomNavView.selectedItemId =
                R.id.navigation_psy_test_record

        }

        // The callback can be enabled or disabled here or in the lambda
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPsyTestResultBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupPsyTestChart()

        viewModel.psyTestEntries.observe(viewLifecycleOwner, Observer {
            it?.let {
                setPsyTestData(it)
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

        viewModel.navigateToPsyTestRecord.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToPsyTestRecordFragment())
                (activity as MainActivity).bottomNavView.selectedItemId =
                    R.id.navigation_psy_test_record
                viewModel.onPsyTestRecordNavigated()
            }
        })



        return binding.root
    }

    private fun setupPsyTestChart() {
        psyTestChart = binding.barChartItemsScore
        psyTestChart.let {

            // disable description
            it.description.isEnabled = false

            it.setExtraOffsets(5f, 10f, 10f, 10f)

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

            it.animateY(1000, Easing.EaseInCubic)

        }
        // set X axis
        val xAxis = psyTestChart.xAxis
        xAxis.let {
            it.position = XAxis.XAxisPosition.BOTTOM
            it.setDrawGridLines(false)
            it.axisLineWidth = 1f
            it.textSize = 15f
            it.valueFormatter = PsyTestFormatter(viewModel.formatValue())
        }

        // set Y axis
        val yAxisLeft = psyTestChart.axisLeft
        yAxisLeft.isEnabled = false

        val yAxisRight = psyTestChart.axisRight
        yAxisRight.let {
            it.textSize = 15f
            it.axisLineWidth = 1f
            it.setDrawGridLines(false)
            it.granularity = 1.0f
            it.isGranularityEnabled = true
        }
        psyTestChart.notifyDataSetChanged()
        psyTestChart.invalidate()
    }

    private fun setPsyTestData(psyTestEntries: List<BarEntry>) {
        val barDataSet = BarDataSet(psyTestEntries, "")

//        barDataSet.setColors(
//            intArrayOf(
//                R.color.normal_range, R.color.light_range, R.color.medium_range,
//                R.color.heavy_range
//            ), context
//        )
        barDataSet.run {
            color = when (viewModel.psyTest.value!!.totalScore) {
                in 0.0..5.0 -> Color.parseColor("#4ab768")
                in 6.0..9.0 -> Color.parseColor("#ffa000")
                in 10.0..14.0 -> Color.parseColor("#d32f2f")
                else -> Color.parseColor("#b71c1c")
            }
            setDrawValues(false)

        }

        psyTestChart.data = BarData(barDataSet)
        psyTestChart.data.barWidth = 0.5f

    }

    class PsyTestFormatter(private val xValsDateLabel: SparseArray<String>) : ValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
            return if (value.toInt() >= 0) {
                xValsDateLabel[value.toInt()]
            } else {
                ("").toString()
            }
        }
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
}