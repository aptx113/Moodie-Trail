package com.danteyu.studio.moodietrail.statistic

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentStatisticBinding
import com.danteyu.studio.moodietrail.ext.FORMAT_DD
import com.danteyu.studio.moodietrail.ext.FORMAT_MM_DD
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.toDisplayFormat
import com.danteyu.studio.moodietrail.login.UserManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class StatisticFragment : Fragment() {

    /**
     * Lazily initialize [StatisticViewModel]
     */
    val viewModel by viewModels<StatisticViewModel> { getVmFactory() }

    private lateinit var binding: FragmentStatisticBinding
    private lateinit var moodChartByMonth: LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStatisticBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupAvgMoodChart()

        viewModel.avgMoodEntries.observe(viewLifecycleOwner, Observer {
            it?.let {
                displayAvgMoodData(it)
            }
        })

        return binding.root
    }

    private fun setupAvgMoodChart() {
        moodChartByMonth = binding.lineChartMoodByMonth
        moodChartByMonth.apply {

            // disable description
            description.isEnabled = false

            setExtraOffsets(5f, 10f, 10f, 10f)

            // enable scaling and dragging
            isDragEnabled = true
            setScaleEnabled(false)

            // disable grid background
            setDrawGridBackground(false)

            // disable legend
            legend.isEnabled = false

            // disable data text
            setNoDataText("")

            setPinchZoom(true)

        }


    }

    private fun displayAvgMoodData(avgMoodEntries: List<Entry>) {

//        val sortedEntries = avgMoodEntries.sortedBy { it.x }
//        for (index in sortedEntries.indices) {
//            sortedEntries[index].x = index.toFloat()
//            if (index == sortedEntries.size -1) sortedEntries[index].x = 10f
//        }
        val dataSet = LineDataSet(avgMoodEntries, getString(R.string.mood))

        dataSet.apply {

            color = Color.parseColor("#ecb220")
            lineWidth = 2f
            circleRadius = 3f

            setDrawCircleHole(false)
            setDrawCircles(true)
            setDrawHorizontalHighlightIndicator(false)
            setDrawHighlightIndicators(false)
            setDrawValues(false)
        }
        // set X axis
        val xAxis = moodChartByMonth.xAxis
        xAxis.apply {

            if (avgMoodEntries.isEmpty()) {
                setDrawAxisLine(false)

            } else {
                setDrawAxisLine(true)
            }
            textSize = 11f
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = TheValueFormatter()
            setDrawGridLines(false)
            axisLineWidth = 2f
            isGranularityEnabled = true
//            granularity = 86400000f
//            axisMinimum = avgMoodEntries.elementAt(0).x
//            setLabelCount(6,true)

//            it.granularity = 86400f
//            it.axisMinimum = (viewModel.getThreeMonthsAgoTimestamp() * 0.9995).toFloat()
//            it.axisMaximum = (viewModel.getNowTimestamp() * 1.0005).toFloat()
//            valueFormatter = (MyValueFormatter(viewModel.formatValue()))

        }

        // set Y axis
        val yAxisRight = moodChartByMonth.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft = moodChartByMonth.axisLeft
        yAxisLeft.apply{

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


        moodChartByMonth.data = LineData(dataSet)
        moodChartByMonth.notifyDataSetChanged()
        moodChartByMonth.isLogEnabled = true
        moodChartByMonth.invalidate()
    }


    class MyValueFormatter(private val xValsDateLabel: SparseArray<String>) : ValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
            if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size() - 1) {
                return xValsDateLabel[value.toInt()]
            } else {
                return ("").toString()
            }
        }
    }

    class TheValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {


//            return if (value.toLong() >= 0 ) {
//                value.toLong().toDisplayFormat(FORMAT_DD)
//            } else {
//                ""
//            }
            return value.toInt().toString()
        }


    }

}