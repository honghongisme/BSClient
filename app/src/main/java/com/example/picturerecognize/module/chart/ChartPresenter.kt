package com.example.picturerecognize.module.chart

import android.content.Context
import com.example.picturerecognize.entity.view.chart.BarChartItem
import com.example.picturerecognize.entity.view.chart.PieChartItem
import com.example.picturerecognize.module.login.UserLocalStore

class ChartPresenter(context: Context, view: ChartView) {

    private var mView = view
    private var mModel : ChartModel? = null
    private var mContext = context.applicationContext

    init {
        mModel = ChartModel(context)
    }

    fun loadData(type : String, range : String, isShowBarChart : Boolean) {
        val id = UserLocalStore.getUserId(mContext)
        if (id == -1) {
            if (isShowBarChart) mView.onGetBarData(mutableListOf(), 0f)
            else mView.onGetPieData(mutableListOf())
        } else {
            mModel?.search(id, type, range, isShowBarChart, object : ChartDataListener {
                override fun onGetBarData(data: MutableList<BarChartItem>, totalAmount: Float) {
                    mView.onGetBarData(data, totalAmount)
                }

                override fun onGetPieData(data: MutableList<PieChartItem>) {
                    mView.onGetPieData(data)
                }

            })
        }

    }

    interface ChartDataListener {
        fun onGetBarData(data: MutableList<BarChartItem>, totalAmount : Float)
        fun onGetPieData(data : MutableList<PieChartItem>)
    }
}