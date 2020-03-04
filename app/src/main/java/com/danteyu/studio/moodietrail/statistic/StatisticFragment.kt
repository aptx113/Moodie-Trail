package com.danteyu.studio.moodietrail.statistic

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentStatisticBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.util.Util.getColor
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
    private lateinit var moodLineChart: LineChart
    private lateinit var moodLineChartInfoDialog: MoodLineChartInfoDialog
    private lateinit var moodPieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStatisticBinding.inflate(inflater, container, false)
        moodLineChartInfoDialog = MoodLineChartInfoDialog()

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.imageStatisticNextMonth.setOnClickListener{
            activity.showToast("Coming Soon")
        }

        binding.imageLastMonthStatistic.setOnClickListener{
            activity.showToast("Coming Soon")
        }

        viewModel.avgMoodEntries.observe(viewLifecycleOwner, Observer {
            it?.let {
                displayAvgMoodData(it)
            }
        })

        viewModel.noteEntries.observe(viewLifecycleOwner, Observer {
            it?.let {
                displayNoteData(it)
            }
        })

        viewModel.showLineChartInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                showLineChartInfoDialog()
                viewModel.onLineChartInfoShowed()
            }
        })
        setupAvgMoodChart()
        setupPieChart()

        return binding.root
    }

    private fun setupAvgMoodChart() {
        moodLineChart = binding.lineChartMoodStatistic
        moodLineChart.apply {

            // disable description
            description.isEnabled = false

            setExtraOffsets(5f, 10f, 10f, 10f)
            animateX(1000, Easing.EaseInBack)
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

    private fun setupPieChart() {
        moodPieChart = binding.pieChartMoodStatistic
        moodPieChart.apply {
            holeRadius = 20f

            setExtraOffsets(0f, 8.5f, 0f, 5f)
            setNoDataText("")
            animateY(1000, Easing.EaseInCubic)
            description.isEnabled = false
            setUsePercentValues(true)
            transparentCircleRadius = 40f
            setTransparentCircleAlpha(50)
            legend.isEnabled = false
            invalidate()
        }
    }

    private fun displayAvgMoodData(avgMoodEntries: List<Entry>) {

        val dataSet = LineDataSet(avgMoodEntries, getString(R.string.mood))

        dataSet.apply {

            color = Color.parseColor("#63a4ff")
            lineWidth = 2f
            circleRadius = 3f

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
            textSize = 11f
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = LineChartXValueFormatter()
            setDrawGridLines(false)
            axisLineWidth = 2f
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

            granularity = 1f
            isGranularityEnabled = true
            axisLineWidth = 1.5f
            textSize = 12f

        }

        moodLineChart.data = LineData(dataSet)
        moodLineChart.notifyDataSetChanged()
        moodLineChart.invalidate()
    }

    private fun displayNoteData(noteEntries: List<PieEntry>) {

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

            valueTextSize = 12f
            valueLinePart1OffsetPercentage = 110f
            valueLinePart1Length = 1f
            valueLinePart2Length = 0.8f
            valueFormatter = PieChartValueFormatter()
            isUsingSliceColorAsValueLineColor = true
            valueLineWidth = 1f
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            sliceSpace = 2f
        }

        moodPieChart.data = PieData(dataSet)
        moodPieChart.notifyDataSetChanged()
        moodPieChart.invalidate()

    }

    private fun showLineChartInfoDialog() {

        parentFragmentManager.let { fragmentManager ->
            if (!moodLineChartInfoDialog.isInLayout) {
                moodLineChartInfoDialog.show(fragmentManager, "Mood Line Chart Info")
            }
        }
    }
}