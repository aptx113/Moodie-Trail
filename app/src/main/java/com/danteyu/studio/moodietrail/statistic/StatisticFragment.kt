package com.danteyu.studio.moodietrail.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.databinding.FragmentStatisticBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.util.Logger
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis

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

        viewModel.averageMood.observe(this, Observer {

            Logger.w("$it")
        })


        setupMoodChartByMonth()

        return binding.root
    }

    private fun setupMoodChartByMonth() {
        moodChartByMonth = binding.lineChartMoodByMonth
        moodChartByMonth.let {
            it.setExtraOffsets(10f, 30f, 10f, 10f)

            // enable scaling and dragging
            it.isDragEnabled = false
            it.setScaleEnabled(false)

            // disable grid background
            it.setDrawGridBackground(false)

            // disable legend
            it.legend.isEnabled = false

            // disable data text
            it.setNoDataText("")
        }

        // set X axis
        val xAxis = moodChartByMonth.xAxis
        xAxis.let {
            it.textSize = 16f
            it.setDrawAxisLine(true)
            it.setDrawGridLines(false)
            it.setDrawLabels(false)
            it.position = XAxis.XAxisPosition.BOTTOM
            it.granularity = 86400f
//            it.axisMinimum = (viewModel.getThreeMonthsAgoTimestamp() * 0.9995).toFloat()
//            it.axisMaximum = (viewModel.getNowTimestamp() * 1.0005).toFloat()
        }

        // set Y axis
        val yAxisRight = moodChartByMonth.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft = moodChartByMonth.axisLeft
        yAxisLeft.let {
            it.isEnabled = true
            it.setDrawAxisLine(true)
            it.axisMaximum = 5.0f
            it.axisMinimum = 1.0f
            it.labelCount = 5


        }

        // disable description
        val description = Description()
        description.isEnabled = false
        moodChartByMonth.description = description
    }
}