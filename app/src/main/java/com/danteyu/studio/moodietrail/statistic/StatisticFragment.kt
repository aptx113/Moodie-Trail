package com.danteyu.studio.moodietrail.statistic

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentStatisticBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getColor
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

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

            // set data
            it.data = viewModel.data()

            // disable description
            it.description.isEnabled = false

            it.setExtraOffsets(10f, 10f, 10f, 10f)

            // enable scaling and dragging
            it.isDragEnabled = false
            it.setScaleEnabled(false)

            // disable grid background
            it.setDrawGridBackground(false)

            // disable legend
            it.legend.isEnabled = false

            // disable data text
            it.setNoDataText("")

            it.setPinchZoom(true)
        }



        // set X axis
        val xAxis = moodChartByMonth.xAxis
        xAxis.let {
            it.textSize = 10f
            it.setDrawAxisLine(true)
            it.setDrawGridLines(false)
            it.setDrawLabels(true)
            it.position = XAxis.XAxisPosition.BOTTOM
            it.setLabelCount(7,true)
//            it.granularity = 86400f
//            it.axisMinimum = (viewModel.getThreeMonthsAgoTimestamp() * 0.9995).toFloat()
//            it.axisMaximum = (viewModel.getNowTimestamp() * 1.0005).toFloat()
            it.valueFormatter = (MyValueFormatter(viewModel.formatValue()))
        }

        // set Y axis
        val yAxisRight = moodChartByMonth.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft = moodChartByMonth.axisLeft
        yAxisLeft.let {
            it.isEnabled = true
            it.axisMaximum = 5.0f
            it.axisMinimum = 1.0f
            it.setLabelCount(5,true)

        }

        moodChartByMonth.invalidate()


    }

    class MyValueFormatter(private val xValsDateLabel: SparseArray<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return super.getFormattedValue(value)
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
            if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size() - 1) {
                return xValsDateLabel[value.toInt()]
            } else {
                return ("").toString()
            }
        }
    }

//    private fun setupBarChartData() {
//        // create BarEntry for Bar Group
//        val bargroup = ArrayList<BarEntry>()
//        bargroup.add(BarEntry(0f, 30f, "0"))
//        bargroup.add(BarEntry(1f, 2f, "1"))
//        bargroup.add(BarEntry(2f, 4f, "2"))
//        bargroup.add(BarEntry(3f, 6f, "3"))
//        bargroup.add(BarEntry(4f, 8f, "4"))
//        bargroup.add(BarEntry(5f, 10f, "5"))
//        bargroup.add(BarEntry(6f, 22f, "6"))
//        bargroup.add(BarEntry(7f, 12.5f, "7"))
//        bargroup.add(BarEntry(8f, 22f, "8"))
//        bargroup.add(BarEntry(9f, 32f, "9"))
//        bargroup.add(BarEntry(10f, 54f, "10"))
//        bargroup.add(BarEntry(11f, 28f, "11"))
//
//        // creating dataset for Bar Group
//        val barDataSet = BarDataSet(bargroup, "")
//
//        barDataSet.color = getColor(R.color.colorAccent)
//
//        val data = BarData(barDataSet)
//
//        binding.lineChartMoodByDay.run {
//            setData(data)
//        }
//        barChart.
//        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        barChart.xAxis.labelCount = 11
//        barChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
//        barChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
//        barChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
//        barChart.description.isEnabled = false
//        barChart.animateY(1000)
//        barChart.legend.isEnabled = false
//        barChart.setPinchZoom(true)
//        barChart.data.setDrawValues(false)
//    }
}