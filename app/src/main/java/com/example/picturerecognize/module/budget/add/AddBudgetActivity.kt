package com.example.picturerecognize.module.budget.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.view.BudgetItem
import com.example.picturerecognize.module.budget.BudgetPresenter
import com.example.picturerecognize.module.budget.BudgetView
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.UserLocalStore
import com.example.picturerecognize.widget.CustomEditText

class AddBudgetActivity : BaseActivity(), View.OnClickListener, BudgetView {

    private var mExpenseTypeTextArray : Array<String> ?= null
    private var mExpenseTypeImageArray : Array<Int> ?= null
    private var mLastSelectItemPosition = -1
    private var mLastSelectItem : View ?= null
    private var mData : MutableList<MutableMap<String, Any?>> ?= null
    private var mAdapter : SimpleAdapter ?= null
    private var mGridView : GridView ?= null
    private val mFrom = arrayOf("image", "text")
    private val mTo = intArrayOf(R.id.image, R.id.text)

    private var mBudgetEt : CustomEditText? = null
    private var mPresenter :  BudgetPresenter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_budget)

        findViewById<TextView>(R.id.cancel_btn).setOnClickListener(this)
        findViewById<TextView>(R.id.add_btn).setOnClickListener(this)
        mBudgetEt = findViewById(R.id.amount_et)
        mGridView = findViewById(R.id.grid)

        mPresenter = BudgetPresenter(this)

        initGridView()
    }

    private fun initGridView() {
        mExpenseTypeTextArray = resources.getStringArray(R.array.expense_type_text)
        mExpenseTypeImageArray = Array(mExpenseTypeTextArray?.size!!) {0}
        val typedArray = resources.obtainTypedArray(R.array.expense_type_image_grey)
        for (i in mExpenseTypeImageArray?.indices!!) {
            mExpenseTypeImageArray!![i] = typedArray.getResourceId(i, 0)
        }
        typedArray.recycle()
        mData = mutableListOf()
        for (index in mExpenseTypeTextArray?.indices!!) {
            val map = mutableMapOf<String, Any?>()
            map["image"] = mExpenseTypeImageArray!![index]
            map["text"] = mExpenseTypeTextArray!![index]
            mData?.add(map)
        }
        mAdapter = SimpleAdapter(this, mData, R.layout.item_type, mFrom, mTo)
        mGridView?.adapter = mAdapter
        mGridView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(this, mExpenseTypeTextArray!![position], Toast.LENGTH_SHORT).show()
                // 如果上一次有选中 且 本次和上次选中的不是一个item 取消上一个item的选中状态 改变当前选中item状态
                if (mLastSelectItemPosition != -1 && mLastSelectItemPosition != position) {
                    mLastSelectItem?.findViewById<ImageView>(R.id.image)?.background = ContextCompat.getDrawable(this, R.drawable.shape_unselect_bg_circular)
                }
                view.findViewById<ImageView>(R.id.image)?.background = ContextCompat.getDrawable(this, R.drawable.shape_select_bg_circular)

                mLastSelectItemPosition = position
                mLastSelectItem = view
            }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cancel_btn -> finish()
            R.id.add_btn -> {
                if (mLastSelectItemPosition == -1) {
                    Toast.makeText(this, "请选择类型！", Toast.LENGTH_SHORT).show()
                    return
                }
                val budget = mBudgetEt?.text.toString()
                if (budget == "" || budget.toFloat() == 0f) {
                    Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show()
                } else {
                    val id = UserLocalStore.getUserId(this@AddBudgetActivity)
                    if (id == -1) {
                        startActivity(Intent(this@AddBudgetActivity, LoginActivity::class.java))
                        Toast.makeText(this@AddBudgetActivity, "请先登录！", Toast.LENGTH_SHORT).show()
                    } else {
                        mPresenter?.addBudget(id, budget.toFloat(), mExpenseTypeTextArray!![mLastSelectItemPosition])
                    }
                }
            }
        }
    }

    override fun onGetData(
        currentMonth: Int,
        totalData: BudgetItem,
        classifyData: MutableList<BudgetItem>
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddSuccess() {
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onAddFailure(msg: String) {
        Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDeleteFailure(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}