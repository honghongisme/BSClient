package com.example.picturerecognize.module.chart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.constans.Constants.CURRENT_WEEK
import com.example.picturerecognize.constans.Constants.EXPENSE
import com.example.picturerecognize.constans.Constants.INCOME
import com.example.picturerecognize.entity.view.chart.BarChartItem
import com.example.picturerecognize.entity.view.chart.PieChartItem
import com.example.picturerecognize.module.chart.payment.ClassifyPaymentActivity
import com.example.picturerecognize.util.NumUtil
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout


class ChartActivity : BaseActivity(), View.OnClickListener, ChartView {

    private var mBackIv : ImageView? = null
    private var mNoDataCl : ConstraintLayout ?= null
    private var mPieChartCl : ConstraintLayout ?= null
    private var mBarChartLl : CoordinatorLayout ?= null
    private var mPieChartBtn : TextView ?= null
    private var mBarChartBtn : TextView ?= null
    private var mTypeBtn : TextView ?= null
    private var mTotalAmount : TextView? = null
    private var mPieChartItemClickShowTv : TextView? = null
    private var mPresenter : ChartPresenter ?= null

    private var mPrevSelectTab = CURRENT_WEEK
    private var mIsShowBarChart = true
    private var mIsSelectExpense = true

    private var mBarChartData : MutableList<BarChartItem>? = null
    private var mBarChartAdapter : BarChartItemAdapter? = null
    private var mPieView : PieChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        mBackIv = findViewById(R.id.back_iv)
        mPieChartBtn = findViewById(R.id.pie_chart_tv)
        mBarChartBtn = findViewById(R.id.bar_chart_tv)
        mTotalAmount = findViewById(R.id.total_amount)
        mPieChartItemClickShowTv = findViewById(R.id.pie_click_show_tv)
        mTypeBtn = findViewById(R.id.type_tv)
        mNoDataCl = findViewById(R.id.no_data_cl)
        mPieChartCl = findViewById(R.id.pie_chart_cl)
        mBarChartLl = findViewById(R.id.bar_chart_ll)

        findViewById<TabLayout>(R.id.tab).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (mPrevSelectTab != tab?.text.toString()) {
                    clearPieChartItemClickShowText()
                    mPrevSelectTab = tab?.text.toString()
                    mPresenter?.loadData(mTypeBtn?.text.toString(), mPrevSelectTab, mIsShowBarChart)
                }
                Toast.makeText(this@ChartActivity, tab?.text, Toast.LENGTH_SHORT).show()
            }
        })
        mPieChartBtn?.setOnClickListener(this)
        mBarChartBtn?.setOnClickListener(this)
        mTypeBtn?.setOnClickListener(this)
        mBackIv?.setOnClickListener(this)
        mPieChartItemClickShowTv?.setOnClickListener(this)

        initBarChart()
        initPieChart()

        mPresenter = ChartPresenter(this, this)
        mPresenter?.loadData(mTypeBtn?.text.toString(), mPrevSelectTab, mIsShowBarChart)
    }

    private fun initBarChart() {
        mBarChartData = mutableListOf()
        val list = findViewById<RecyclerView>(R.id.list)
        list?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBarChartAdapter = BarChartItemAdapter(this, mBarChartData!!)
        list?.adapter = mBarChartAdapter
        mBarChartAdapter?.addItemClickListener(object : BarChartItemAdapter.OnItemClickListener{
            override fun onClick(classify : String) {
                startClassPaymentActivity(classify)
            }
        })
    }

    private fun initPieChart() {
        mPieView = findViewById(R.id.pie_view)
        mPieView?.setEntryLabelColor(R.color.black1)
        // 设置比例图
        val mLegend = mPieView?.legend
        mLegend?.formSize = 12f
        // 换行
        mLegend?.isWordWrapEnabled = true
        mLegend?.direction = Legend.LegendDirection.LEFT_TO_RIGHT

        mLegend?.textColor = resources.getColor(R.color.black1)
        mLegend?.textSize = 14f
        mLegend?.formToTextSpace = 8f
        mLegend?.xEntrySpace = 10f
        mLegend?.yEntrySpace = 10f
        mLegend?.form = Legend.LegendForm.SQUARE //比例块形状，默认为方块
        mPieView?.extraTopOffset = 20f
        mPieView?.extraBottomOffset = 20f
        // 取消右下角的description label
        mPieView?.description?.isEnabled = false
        mPieView?.setEntryLabelTextSize(12f)
        // 中间空心太大 默认50 55 这里改成40 45
        mPieView?.holeRadius = 35f
        mPieView?.transparentCircleRadius = 40f
        // value百分比显示
        mPieView?.setUsePercentValues(true)
        mPieView?.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                mPieChartItemClickShowTv?.visibility = View.INVISIBLE
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val entry = e as PieEntry
                mPieChartItemClickShowTv?.text = "${entry.label} ${NumUtil.keepOneDecimalPlace(entry.y * 100)}%"
                mPieChartItemClickShowTv?.visibility = View.VISIBLE
            }

        })

        mPieView?.invalidate()

    }

    private fun clearPieChartItemClickShowText() {
        mPieChartItemClickShowTv?.visibility = View.INVISIBLE
    }

    private fun startClassPaymentActivity(classify : String) {
        val intent = Intent(this@ChartActivity, ClassifyPaymentActivity::class.java)
        intent.putExtra(Constants.RANGE, mPrevSelectTab)
        intent.putExtra(Constants.TYPE, mTypeBtn?.text.toString())
        intent.putExtra(Constants.CLASSIFY, classify)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.back_iv -> finish()
            R.id.pie_chart_tv -> {
                if (mIsShowBarChart) {
                    mIsShowBarChart = false
                    mPieChartBtn?.setTextColor(this@ChartActivity.resources.getColor(R.color.chart_switch_select_text))
                    mBarChartBtn?.setTextColor(this@ChartActivity.resources.getColor(R.color.chart_switch_not_select_text))
                    mPresenter?.loadData(mTypeBtn?.text.toString(), mPrevSelectTab, mIsShowBarChart)
                }
                Toast.makeText(this@ChartActivity, "pie", Toast.LENGTH_SHORT).show()
            }
            R.id.bar_chart_tv -> {
                if (!mIsShowBarChart) {
                    mIsShowBarChart = true
                    mBarChartBtn?.setTextColor(this@ChartActivity.resources.getColor(R.color.chart_switch_select_text))
                    mPieChartBtn?.setTextColor(this@ChartActivity.resources.getColor(R.color.chart_switch_not_select_text))
                    mPresenter?.loadData(mTypeBtn?.text.toString(), mPrevSelectTab, mIsShowBarChart)
                }
                Toast.makeText(this@ChartActivity, "bar", Toast.LENGTH_SHORT).show()
            }
            R.id.type_tv -> {
                clearPieChartItemClickShowText()
                if (mIsSelectExpense) {
                    mTypeBtn?.text = INCOME
                    mIsSelectExpense = false
                } else {
                    mTypeBtn?.text = EXPENSE
                    mIsSelectExpense = true
                }
                Toast.makeText(this@ChartActivity, mTypeBtn?.text.toString(), Toast.LENGTH_SHORT).show()
                mPresenter?.loadData(mTypeBtn?.text.toString(), mPrevSelectTab, mIsShowBarChart)
            }
            R.id.pie_click_show_tv -> {
                startClassPaymentActivity((v as TextView).text.toString().split(" ")[0])
            }
        }
    }

    override fun onGetBarData(data: MutableList<BarChartItem>, totalAmount : Float) {
        if (data.size == 0) {
            mNoDataCl?.visibility = View.VISIBLE
            mBarChartLl?.visibility = View.GONE
            mPieChartCl?.visibility = View.GONE
        } else {
            mNoDataCl?.visibility = View.GONE
            mBarChartLl?.visibility = View.VISIBLE
            mPieChartCl?.visibility = View.GONE

            mBarChartData?.clear()
            mBarChartData?.addAll(data)
            mBarChartAdapter?.notifyDataSetChanged()
            mTotalAmount?.text = totalAmount.toString()
        }
    }

    override fun onGetPieData(data: MutableList<PieChartItem>) {
        if (data.size == 0) {
            mNoDataCl?.visibility = View.VISIBLE
            mBarChartLl?.visibility = View.GONE
            mPieChartCl?.visibility = View.GONE
        } else {
            mNoDataCl?.visibility = View.GONE
            mBarChartLl?.visibility = View.GONE
            mPieChartCl?.visibility = View.VISIBLE

/*            val pieEntries: MutableList<HollowPieEntry> = ArrayList()
            for (item in data) {
                //参数1:扇形区域占比,参数2:饼图内文字,参数3:指示线文字
                pieEntries.add(HollowPieEntry(item.proportion / 100, "${item.amount}", "${item.classify} ${item.proportion}%"))
            }
            mPieView?.setHollowPieEntries(pieEntries)*/

            // 添加数据和颜色
            val entries = mutableListOf<PieEntry>()
            val colors = mutableListOf<Int>()
            val typedArray = resources.obtainTypedArray(R.array.bar_color)
            for ((index, item) in data.withIndex()) {
                entries.add(PieEntry(item.proportion / 100, item.classify))
                colors.add(typedArray.getColor(index, 0))
            }
            typedArray.recycle()
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = colors
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = R.color.black
            dataSet.valueLinePart1OffsetPercentage = 70f
            dataSet.valueLinePart1Length = 0.7f
            dataSet.valueLinePart2Length = 0.8f
            dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            dataSet.isUsingSliceColorAsValueLineColor = true
            dataSet.selectionShift = 5f

            val pieData = PieData(dataSet)
            pieData.setValueFormatter(PercentFormatter(mPieView))

            mPieView?.data = pieData
            mPieView?.invalidate()
        }
    }
}
