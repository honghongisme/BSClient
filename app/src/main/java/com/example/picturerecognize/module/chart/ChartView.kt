package com.example.picturerecognize.module.chart

import com.example.picturerecognize.entity.view.chart.BarChartItem
import com.example.picturerecognize.entity.view.chart.PieChartItem

interface ChartView {

    fun onGetBarData(data: MutableList<BarChartItem>, totalAmount : Float)
    fun onGetPieData(data : MutableList<PieChartItem>)
}