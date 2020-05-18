package com.example.picturerecognize.module.bill

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.util.DateUtil
import java.util.*

class BillActivity : BaseActivity(), View.OnClickListener, BillView {

    private var mYearTv : TextView ?= null
    private var mData : MutableList<Bill> ?= null
    private var mBalanceTv : TextView ?= null
    private var mIncomeTv : TextView ?= null
    private var mExpenseTv : TextView ?= null
    private var mList : RecyclerView ?= null
    private var mAdapter : BillListAdapter ?= null
    private var mPresenter : BillPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        mYearTv = findViewById(R.id.year_tv)
        mList = findViewById(R.id.list)
        mIncomeTv = findViewById(R.id.income_tv)
        mBalanceTv = findViewById(R.id.balance_tv)
        mExpenseTv = findViewById(R.id.expense_tv)

        findViewById<ImageView>(R.id.back_iv).setOnClickListener(this)
        mYearTv?.setOnClickListener(this)

        mData = mutableListOf()
        mList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = BillListAdapter(mData!!)
        mAdapter?.addItemClickListener(object : BillListAdapter.OnItemClickListener {
            override fun onClick() {
                Toast.makeText(this@BillActivity, "click", Toast.LENGTH_SHORT).show()
            }
        })
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.color.divider, null))
        mList?.addItemDecoration(decoration)
        mList?.adapter = mAdapter

        val year = DateUtil.getCurrentYear().toString()
        mYearTv?.text = "${year}年"
        mPresenter = BillPresenter(this, this)
        mPresenter?.loadData(year)
    }

    private fun showDatePickDialog() {
        TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->
                val cal = Calendar.getInstance()
                cal.time = date
                mYearTv?.text = "${cal.get(Calendar.YEAR)}年"

                mPresenter?.loadData(cal.get(Calendar.YEAR).toString())
            })
            .setType(arrayOf(true, false, false, false, false, false).toBooleanArray())// 默认全部显示
            .setCancelText(resources.getString(R.string.cancel))//取消按钮文字
            .setSubmitText(resources.getString(R.string.determine))//确认按钮文字
            .setContentTextSize(15)//滚轮文字大小
            .setTitleSize(15)//标题文字大小
            .setTitleText(resources.getString(R.string.select_month))//标题文字
            .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true)//是否循环滚动
            .setLineSpacingMultiplier(25f)
            .isAlphaGradient(true)
            .setItemVisibleCount(5)
            .setSubCalSize(15)
            //     .setTitleColor(Color.BLACK)//标题文字颜色
            .setSubmitColor(Color.BLACK)//确定按钮文字颜色
            .setCancelColor(Color.BLACK)//取消按钮文字颜色
            //     .setTitleBgColor(0xFF666666.toInt())//标题背景颜色 Night mode
            //      .setBgColor(0xFF333333.toInt())//滚轮背景颜色 Night mode
            .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .isDialog(false)//是否显示为对话框样式
            .setOutSideCancelable(true)
            .build()
            .show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_iv -> finish()
            R.id.year_tv -> showDatePickDialog()
        }
    }

    override fun onGetData(
        billList: MutableList<Bill>,
        yearIncome: Float,
        yearExpense: Float,
        yearBalance: Float
    ) {
        mData?.clear()
        mData?.addAll(billList)
        mAdapter?.notifyDataSetChanged()

        mIncomeTv?.text = yearIncome.toString()
        mExpenseTv?.text = yearExpense.toString()
        mBalanceTv?.text = yearBalance.toString()
    }
}
