package com.example.picturerecognize.module.budget

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.tu.circlelibrary.CirclePercentBar
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.constans.Constants.CLASSIFY_MONTH_TOTAL
import com.example.picturerecognize.entity.view.BudgetItem
import com.example.picturerecognize.module.budget.add.AddBudgetActivity
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.UserLocalStore
import com.example.picturerecognize.widget.CustomEditText

class BudgetActivity : BaseActivity(), BudgetView, View.OnClickListener {

    private var mTotalContainerCL : ConstraintLayout? = null
    private var mAddClassifyBudgetTv : TextView? = null
    private var mBudgetTitleTv : TextView? = null
    private var mRemainBudgetTv : TextView? = null
    private var mCurrentMonthBudgetTv : TextView? = null
    private var mCurrentMonthExpenseTv : TextView? = null
    private var mRing : CirclePercentBar? = null

    private var mNoDataCl : ConstraintLayout? = null
    private var mList : RecyclerView? = null
    private var mData : MutableList<BudgetItem>? = null
    private var mAdapter : BudgetAdapter? = null

    private var mPresenter : BudgetPresenter? = null

    private var mBottomMenuDialog : Dialog? = null
    private var mBudgetSetDialog : AlertDialog? = null
    private var mBudgetEt : CustomEditText? = null
    private var mCurrentSetBudgetClassify = CLASSIFY_MONTH_TOTAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        mAddClassifyBudgetTv = findViewById(R.id.add_classify_budget_tv)
        mTotalContainerCL = findViewById(R.id.total_budget_sv)
        mBudgetTitleTv = findViewById(R.id.describe_title_tv)
        mRemainBudgetTv = findViewById(R.id.remain_budget_tv)
        mCurrentMonthBudgetTv = findViewById(R.id.month_budget_tv)
        mCurrentMonthExpenseTv = findViewById(R.id.month_expense_tv)
        mTotalContainerCL = findViewById(R.id.total_budget_sv)
        mRing = findViewById(R.id.ring)
        mNoDataCl = findViewById(R.id.no_data_cl)
        mList = findViewById(R.id.list)
        findViewById<ImageView>(R.id.back_iv).setOnClickListener(this)

        mData = mutableListOf()
        mList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = BudgetAdapter(mData!!)
        mAdapter?.addItemClickListener(object : BudgetAdapter.OnItemClickListener {
            override fun onClick(classify : String) {
                mCurrentSetBudgetClassify = classify
                popBottomMenuDialog()
            }
        })
        mList?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mList?.adapter = mAdapter

        mTotalContainerCL?.setOnClickListener(this)
        mAddClassifyBudgetTv?.setOnClickListener(this)

        mPresenter = BudgetPresenter(this)

    }

    private fun popBottomMenuDialog() {
        mBottomMenuDialog = Dialog(this, R.style.BottomDialog)
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_budget_menu, null)
        layout.findViewById<TextView>(R.id.edit_tv).setOnClickListener(this)
        layout.findViewById<TextView>(R.id.delete_tv).setOnClickListener(this)
        layout.findViewById<TextView>(R.id.budget_set_cancel_tv).setOnClickListener(this)
        mBottomMenuDialog!!.setContentView(layout)
        val dialogWindow = mBottomMenuDialog!!.window
        dialogWindow?.setGravity(Gravity.BOTTOM)
        val lp = dialogWindow?.attributes
        lp?.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        mBottomMenuDialog!!.show()
    }

    private fun popEditBudgetDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_set_budget, null)
        layout.findViewById<TextView>(R.id.budget_set_cancel_tv).setOnClickListener(this)
        layout.findViewById<TextView>(R.id.budget_set_determine_tv).setOnClickListener(this)
        mBudgetEt = layout.findViewById(R.id.amount_et)
        dialogBuilder.setView(layout)
        mBudgetSetDialog = dialogBuilder.create()
        mBudgetSetDialog?.show()
    }

    override fun onGetData(currentMonth : Int, totalData: BudgetItem, classifyData: MutableList<BudgetItem>) {
        mBudgetTitleTv?.text = "${currentMonth}月总预算"
        mRemainBudgetTv?.text = totalData.remainBudget.toString()
        mCurrentMonthBudgetTv?.text = totalData.budget.toString()
        mCurrentMonthExpenseTv?.text = totalData.expense.toString()
        mRing?.setPercentData(totalData.proportion, DecelerateInterpolator())


        // classifyData
        if (classifyData.size == 0) {
            mNoDataCl?.visibility = View.VISIBLE
            mList?.visibility = View.GONE
        } else {
            mNoDataCl?.visibility = View.GONE
            mList?.visibility = View.VISIBLE

            mData?.clear()
            mData?.addAll(classifyData)
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onAddSuccess() {
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
        mPresenter?.loadData()
    }

    override fun onAddFailure(msg: String) {
        Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteSuccess() {
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
        mPresenter?.loadData()
    }

    override fun onDeleteFailure(msg: String) {
        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.add_classify_budget_tv -> {
                startActivity(Intent(this@BudgetActivity, AddBudgetActivity::class.java))
            }
            R.id.total_budget_sv -> {
                mCurrentSetBudgetClassify = CLASSIFY_MONTH_TOTAL
                popBottomMenuDialog()
            }
            R.id.edit_tv -> {
                mBottomMenuDialog?.dismiss()
                popEditBudgetDialog()
            }
            R.id.delete_tv -> {
                mBottomMenuDialog?.dismiss()
                val id = UserLocalStore.getUserId(this@BudgetActivity)
                if (id == -1) {
                    startActivity(Intent(this@BudgetActivity, LoginActivity::class.java))
                    Toast.makeText(this@BudgetActivity, "请先登录！", Toast.LENGTH_SHORT).show()
                } else {
                    mPresenter?.deleteBudget(id, mCurrentSetBudgetClassify)
                    Toast.makeText(this@BudgetActivity, "删除成功！", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.budget_set_cancel_tv -> {
                mBudgetSetDialog?.dismiss()
            }
            R.id.budget_set_determine_tv -> {
                val budget = mBudgetEt?.text.toString()
                if (budget == "null" || budget.toFloat() == 0f) {
                    Toast.makeText(this@BudgetActivity, "请输入预算金额！", Toast.LENGTH_SHORT).show()
                } else {
                    mBudgetSetDialog?.dismiss()
                    val id = UserLocalStore.getUserId(this@BudgetActivity)
                    if (id == -1) {
                        startActivity(Intent(this@BudgetActivity, LoginActivity::class.java))
                        Toast.makeText(this@BudgetActivity, "请先登录！", Toast.LENGTH_SHORT).show()
                    } else {
                        mPresenter?.addBudget(id, budget.toFloat(), mCurrentSetBudgetClassify)
                    }
                }
            }
            R.id.back_iv -> finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.loadData()
    }
}
