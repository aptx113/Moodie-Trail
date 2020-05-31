package com.danteyu.studio.moodietrail.ui.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentStatisticBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.util.Util.getColor
import com.danteyu.studio.moodietrail.util.Util.getDimensionPixelSize
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*

class StatisticFragment : Fragment() {

    /**
     * Lazily initialize [StatisticViewModel]
     */
    val viewModel by viewModels<StatisticViewModel> { getVmFactory() }

    private lateinit var binding: FragmentStatisticBinding
    private lateinit var moodLineChartInfoDialog: MoodLineChartInfoDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStatisticBinding.inflate(inflater, container, false)
        moodLineChartInfoDialog = MoodLineChartInfoDialog()

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.avgMoodScoreEntries.observe(viewLifecycleOwner, Observer {
            it?.let {
                displayAvgMoodScoresData(it, binding.lineChartMoodStatistic)
            }
        })

        viewModel.noteEntries.observe(viewLifecycleOwner, Observer {
            it?.let {
                displayNotesData(it, binding.pieChartMoodStatistic)
            }
        })

        viewModel.showLineChartInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                showLineChartInfoDialog()
                viewModel.onLineChartInfoShowed()
            }
        })
        setupAvgMoodScoresChart(binding.lineChartMoodStatistic)
        setupPieChart(binding.pieChartMoodStatistic)

        return binding.root
    }

    private fun setupAvgMoodScoresChart(moodLineChart: LineChart) {

        moodLineChart.apply {
            // disable description
            description.isEnabled = false

            setExtraOffsets(
                getDimensionPixelSize(R.dimen.offset_left_line_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_top_line_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_right_line_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_bottom_line_chart).toFloat()
            )
            animateX(ANIMATE_DURATION_MILLIS, Easing.EaseInBack)
            // enable scaling and dragging
            isDragEnabled = true

            // disable grid background
            setDrawGridBackground(false)

            // disable legend
            legend.isEnabled = false
            setScaleEnabled(false)
            setTouchEnabled(true)

            // disable data text
            setNoDataText("")

            setPinchZoom(false)
        }
    }

    private fun setupPieChart(moodPieChart: PieChart) {

        moodPieChart.apply {
            holeRadius = getDimensionPixelSize(R.dimen.radius_hole_pie_chart).toFloat()

            setExtraOffsets(
                getDimensionPixelSize(R.dimen.offset_left_pie_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_top_pie_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_right_pie_chart).toFloat(),
                getDimensionPixelSize(R.dimen.offset_bottom_pie_chart).toFloat()
            )
            setNoDataText("")
            animateY(ANIMATE_DURATION_MILLIS, Easing.EaseInCubic)
            description.isEnabled = false
            setUsePercentValues(true)
            transparentCircleRadius =
                getDimensionPixelSize(R.dimen.radius_transparent_circle_pie_chart).toFloat()
            setTransparentCircleAlpha(getDimensionPixelSize(R.dimen.alpha_transparent_circle_pie_chart))
            legend.isEnabled = false
            invalidate()
        }
    }

    private fun displayAvgMoodScoresData(avgMoodEntries: List<Entry>, moodLineChart: LineChart) {

        val dataSet = LineDataSet(avgMoodEntries, getString(R.string.mood))

        dataSet.apply {

            color = MoodieTrailApplication.instance.getColor(R.color.blue_700)
            lineWidth = getDimensionPixelSize(R.dimen.width_line_chart_data_set_line).toFloat()
            circleRadius =
                getDimensionPixelSize(R.dimen.radius_line_chart_data_set_circle).toFloat()

            setDrawCircleHole(false)
            setDrawCircles(true)
            setDrawHorizontalHighlightIndicator(false)
            setDrawHighlightIndicators(false)
            setDrawValues(false)
        }
        // set X axis
        val xAxis = moodLineChart.xAxis
        xAxis.apply {

            if (avgMoodEntries.isEmpty()) {
                setDrawAxisLine(false)

            } else {
                setDrawAxisLine(true)
            }
            textSize = getDimensionPixelSize(R.dimen.size_text_value_chart).toFloat()
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = LineChartXValueFormatter()
            setDrawGridLines(false)
            axisLineWidth = getDimensionPixelSize(R.dimen.width_line_chart_axis_line).toFloat()
            isGranularityEnabled = true

        }

        // set Y axis
        val yAxisRight = moodLineChart.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft = moodLineChart.axisLeft
        yAxisLeft.apply {

            if (avgMoodEntries.isEmpty()) {
                setDrawAxisLine(false)

            } else {
                setDrawAxisLine(true)
            }

            isGranularityEnabled = true
            axisLineWidth = getDimensionPixelSize(R.dimen.width_line_chart_axis_line).toFloat()
            textSize = getDimensionPixelSize(R.dimen.size_text_value_chart).toFloat()

        }
        moodLineChart.data = LineData(dataSet)
        moodLineChart.notifyDataSetChanged()
        moodLineChart.invalidate()
    }

    private fun displayNotesData(noteEntries: List<PieEntry>, moodPieChart: PieChart) {

        val colors = ArrayList<Int>()
        val colorTable = listOf(
            getColor(R.color.mood_very_bad),
            getColor(R.color.mood_bad),
            getColor(R.color.mood_normal),
            getColor(R.color.mood_good),
            getColor(R.color.mood_very_good)
        )
        for (color in colorTable) {
            colors.add(color)
        }

        val dataSet = PieDataSet(noteEntries, "")

        dataSet.colors = colors
        dataSet.apply {

            valueTextSize = getDimensionPixelSize(R.dimen.size_text_value_chart).toFloat()
            valueLinePart1OffsetPercentage =
                getDimensionPixelSize(R.dimen.percentage_value_line_part_1_offset).toFloat()
            valueLinePart1Length =
                getDimensionPixelSize(R.dimen.length_pie_chart_value_line_part_1).toFloat()
            valueLinePart2Length =
                getDimensionPixelSize(R.dimen.length_pie_chart_value_line_part_2).toFloat()
            valueFormatter = PieChartValueFormatter()
            isUsingSliceColorAsValueLineColor = true
            valueLineWidth =
                getDimensionPixelSize(R.dimen.width_pie_chart_value_line).toFloat()

            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            sliceSpace = getDimensionPixelSize(R.dimen.size_slice_space_pie_chart).toFloat()
        }
        moodPieChart.data = PieData(dataSet)
        moodPieChart.notifyDataSetChanged()
        moodPieChart.invalidate()
    }

    private fun showLineChartInfoDialog() {

        childFragmentManager.let { fragmentManager ->
            if (!moodLineChartInfoDialog.isInLayout) {
                moodLineChartInfoDialog.show(fragmentManager, LINE_CHART_INFO_TAG)
            }
        }
    }

    companion object {
        const val LINE_CHART_INFO_TAG = "Mood Line Chart Info"
        const val ANIMATE_DURATION_MILLIS = 1000
    }
}