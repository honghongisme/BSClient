package com.example.picturerecognize.module.add

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.constans.Constants.EXPENSE
import com.example.picturerecognize.constans.Constants.INCOME
import com.example.picturerecognize.entity.bean.Record
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_AMOUNT
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_CLASSIFY
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_DATE
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_ID
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_NOTE
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_START_ORIGIN
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_TYPE
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_VALUE_START_ORIGIN_EDIT
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_VALUE_START_ORIGIN_MANUAL_INPUT
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_VALUE_START_ORIGIN_RECOGNIZE
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_VALUE_START_ORIGIN_TRAIN
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.UserLocalStore
import com.example.picturerecognize.util.DateUtil
import com.example.picturerecognize.widget.CustomEditText
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class AddActivity : BaseActivity(), View.OnClickListener, AddView {

    private var mFrom = arrayOf("image", "text")
    private var mTo = intArrayOf(R.id.image, R.id.text)
    private var mAdapter : SimpleAdapter ?= null
    private var mData : MutableList<MutableMap<String, Any?>> ?= null
    private var mExpenseTypeTextArray : Array<String> ?= null
    private var mExpenseTypeImageArray : Array<Int> ?= null
    private var mIncomeTypeTextArray : Array<String> ?= null
    private var mIncomeTypeImageArray : Array<Int> ?= null
    private var mIsSelectExpense : Boolean = true
    private var mLastSelectItemPosition = -1
    private var mLastSelectItem : View ?= null

    private var mGridView :GridView ?= null
    private var mExpenseTv : TextView ?= null
    private var mIncomeTv : TextView ?= null
    private var mAmountEt : CustomEditText ?= null
    private var mNoteEt : EditText ?= null
    private var mDateTv : TextView ?= null
    private var mDatePicker : MaterialCalendarView ?= null

    private var mPresenter : AddPresenter ?= null

    private var mOperateMode : String ?= null
    private var mId = -1
    private var mDate : String ?= null
    private var mType : String ?= null
    private var mAmount = 0f
    private var mNote : String ?= null
    private var mClassify : String ?= null




    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.expense_tv -> {
                if (!mIsSelectExpense) {
                    mIsSelectExpense = true
                    mExpenseTv?.setTextColor(this@AddActivity.resources.getColor(R.color.black1))
                    mIncomeTv?.setTextColor(this@AddActivity.resources.getColor(R.color.item_unselect_bg))
                    clearItemSelectStatus()
                    initExpenseView()
                }
            }
            R.id.current_month_income_tv -> {
                if (mIsSelectExpense) {
                    mIsSelectExpense = false
                    mIncomeTv?.setTextColor(this@AddActivity.resources.getColor(R.color.black1))
                    mExpenseTv?.setTextColor(this@AddActivity.resources.getColor(R.color.item_unselect_bg))
                    clearItemSelectStatus()
                    initIncomeView()
                }
            }
            R.id.budget_set_cancel_tv -> {
                Toast.makeText(this@AddActivity, "cancel", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.save_tv -> {
                when (mOperateMode) {
                    INTENT_EXTRA_VALUE_START_ORIGIN_MANUAL_INPUT -> {
                        doSave()
                    }
                    INTENT_EXTRA_VALUE_START_ORIGIN_EDIT -> {
                        if (isInputValid()) {
                            val amount = mAmountEt?.text.toString().toFloat()
                            var type : String ?= null
                            var classify : String ?= null
                            if (mIsSelectExpense) {
                                type = "支出"
                                classify = mExpenseTypeTextArray!![mLastSelectItemPosition]
                            } else {
                                type = "收入"
                                classify = mIncomeTypeTextArray!![mLastSelectItemPosition]
                            }
                            var note = mNoteEt?.text.toString()
                            if ("" == note) {
                                note = classify
                            }
                            val date = mDateTv?.text.toString()

                            val record = Record(mId, 0, type, classify, amount, date, note)
                            mPresenter?.update(record)
                        }
                    }
                    INTENT_EXTRA_VALUE_START_ORIGIN_RECOGNIZE -> {
                        doSave()
                    }
                    INTENT_EXTRA_VALUE_START_ORIGIN_TRAIN -> {
                        doSave()
                    }
                }

            }
            R.id.date_tv -> {
                // 弹出date picker  选择合适的 日期选择器  年月日 星期
                showDatePicker()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        mGridView = findViewById(R.id.grid)
        mExpenseTv = findViewById(R.id.expense_tv)
        mIncomeTv = findViewById(R.id.current_month_income_tv)
        mAmountEt = findViewById(R.id.amount_et)
        mNoteEt = findViewById(R.id.note_et)
        mDateTv = findViewById(R.id.date_tv)

        // get data
        mOperateMode = intent.getStringExtra(INTENT_EXTRA_KEY_START_ORIGIN)
        mAmount = intent.getFloatExtra(INTENT_EXTRA_KEY_AMOUNT, 0f)
        mDate = intent.getStringExtra(INTENT_EXTRA_KEY_DATE)
        mNote = intent.getStringExtra(INTENT_EXTRA_KEY_NOTE)
        mClassify = intent.getStringExtra(INTENT_EXTRA_KEY_CLASSIFY)
        mType =  intent.getStringExtra(INTENT_EXTRA_KEY_TYPE)
        mId = intent.getIntExtra(INTENT_EXTRA_KEY_ID, -1)

        if (mDate == null) mDate = DateUtil.getCurrentDate("yyyy-MM-dd")
        if (mNote == null) mNote = ""
        if (mType == null) mType = EXPENSE

        // init data
        mAmountEt?.text = Editable.Factory.getInstance().newEditable(mAmount.toString())
        mDateTv?.text = mDate
        mNoteEt?.text = Editable.Factory.getInstance().newEditable(mNote)

        when (mType) {
            EXPENSE -> {
                selectExpenseItem()
            }
            INCOME -> {
                selectIncomeItem()
            }
        }

        mPresenter = AddPresenter(this)

        findViewById<TextView>(R.id.budget_set_cancel_tv).setOnClickListener(this)
        findViewById<TextView>(R.id.save_tv).setOnClickListener(this)
        mExpenseTv?.setOnClickListener(this)
        mIncomeTv?.setOnClickListener(this)
        mDateTv?.setOnClickListener(this)
    }

    private fun doSave() {
        if (isInputValid()) {
            val amount = mAmountEt?.text.toString().toFloat()
            var type : String ?= null
            var classify : String ?= null
            if (mIsSelectExpense) {
                type = "支出"
                classify = mExpenseTypeTextArray!![mLastSelectItemPosition]
            } else {
                type = "收入"
                classify = mIncomeTypeTextArray!![mLastSelectItemPosition]
            }
            var note = mNoteEt?.text.toString()
            if ("" == note) {
                note = classify
            }

            val userId = UserLocalStore.getUserId(this@AddActivity)
            if (userId == -1) {
                Toast.makeText(this@AddActivity, "请先登录", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AddActivity, LoginActivity::class.java))
            } else {
                val date = mDateTv?.text.toString()
                mPresenter?.add(Record(
                    0,
                    userId,
                    type,
                    classify,
                    amount,
                    date,
                    note
                ))
            }

        }
    }

    /**
     * 初始化时 支出item 默认选中
     */
    private fun selectExpenseItem() {
        initExpenseView()
        selectExpenseType()
        // 设置选中的classify
        if (mClassify != null) {
            mGridView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mGridView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    // 设置选中的classify
                    val position = mExpenseTypeTextArray?.indexOf(mClassify)
                    setDefaultSelect(position)
                }
            })
        }
    }

    private fun selectIncomeItem() {
        initIncomeView()
        selectIncomeType()
        // 设置选中的classify
        if (mClassify != null) {
            mGridView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mGridView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    val position = mIncomeTypeTextArray?.indexOf(mClassify)
                    setDefaultSelect(position)
                }
            })
        }
    }

    private fun selectExpenseType() {
        mIsSelectExpense = true
        mExpenseTv?.setTextColor(this@AddActivity.resources.getColor(R.color.type_select_text))
        mIncomeTv?.setTextColor(this@AddActivity.resources.getColor(R.color.type_unselect_text))
    }

    private fun selectIncomeType() {
        mIsSelectExpense = false
        mIncomeTv?.setTextColor(this@AddActivity.resources.getColor(R.color.type_select_text))
        mExpenseTv?.setTextColor(this@AddActivity.resources.getColor(R.color.type_unselect_text))
    }

    private fun setDefaultSelect(position : Int?) {
        if (position != null && position != -1) {
            val itemView = position.let { mGridView?.getChildAt(it) }
            itemView?.findViewById<ImageView>(R.id.image)?.background = ContextCompat.getDrawable(this, R.drawable.shape_select_bg_circular)
            mLastSelectItemPosition = position
            mLastSelectItem = itemView
        }
    }

    private fun initExpenseView() {
        if (mExpenseTypeTextArray == null) {
            mExpenseTypeTextArray = resources.getStringArray(R.array.expense_type_text)
            mExpenseTypeImageArray = Array(mExpenseTypeTextArray?.size!!) {0}
            val typedArray = resources.obtainTypedArray(R.array.expense_type_image_grey)
            for (i in mExpenseTypeImageArray?.indices!!) {
                mExpenseTypeImageArray!![i] = typedArray.getResourceId(i, 0)
            }
            typedArray.recycle()
        }
        mData = mutableListOf()
        for (index in mExpenseTypeTextArray?.indices!!) {
            val map = mutableMapOf<String, Any?>()
            map["image"] = mExpenseTypeImageArray!![index]
            map["text"] = mExpenseTypeTextArray!![index]
            mData?.add(map)
        }
        mAdapter = SimpleAdapter(this@AddActivity, mData, R.layout.item_type, mFrom, mTo)
        mGridView?.adapter = mAdapter
        mGridView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // 如果上一次有选中 且 本次和上次选中的不是一个item 取消上一个item的选中状态 改变当前选中item状态
                if (mLastSelectItemPosition != -1 && mLastSelectItemPosition != position) {
                    mLastSelectItem?.findViewById<ImageView>(R.id.image)?.background = ContextCompat.getDrawable(this, R.drawable.shape_unselect_bg_circular)
                }
                view.findViewById<ImageView>(R.id.image)?.background = ContextCompat.getDrawable(this, R.drawable.shape_select_bg_circular)

                mLastSelectItemPosition = position
                mLastSelectItem = view
            }
    }

    private fun initIncomeView() {
        if (mIncomeTypeTextArray == null) {
            mIncomeTypeTextArray = resources.getStringArray(R.array.income_type_text)
            mIncomeTypeImageArray = Array(mIncomeTypeTextArray?.size!!) {0}
            val typedArray = resources.obtainTypedArray(R.array.income_type_image_grey)
            for (i in mIncomeTypeImageArray?.indices!!) {
                mIncomeTypeImageArray!![i] = typedArray.getResourceId(i, 0)
            }
            typedArray.recycle()
        }
        mData = mutableListOf()
        for (index in mIncomeTypeImageArray?.indices!!) {
            val map = mutableMapOf<String, Any?>()
            map["image"] = mIncomeTypeImageArray!![index]
            map["text"] = mIncomeTypeTextArray!![index]
            mData?.add(map)
        }
        mAdapter = SimpleAdapter(this@AddActivity, mData, R.layout.item_type, mFrom, mTo)
        mGridView?.adapter = mAdapter
        mGridView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // 如果上一次有选中 且 本次和上次选中的不是一个item 取消上一个item的选中状态 改变当前选中item状态
                if (mLastSelectItemPosition != -1 && mLastSelectItemPosition != position) {
                    mLastSelectItem?.findViewById<ImageView>(R.id.image)?.background = ContextCompat.getDrawable(this, R.drawable.shape_unselect_bg_circular)
                }
                view.findViewById<ImageView>(R.id.image)?.background = ContextCompat.getDrawable(this, R.drawable.shape_select_bg_circular)

                mLastSelectItemPosition = position
                mLastSelectItem = view
            }
    }

    private fun clearItemSelectStatus() {
        mLastSelectItemPosition = -1
        mLastSelectItem = null
    }

    private fun isInputValid() : Boolean {
        if ("" == mAmountEt?.text.toString()) {
            Toast.makeText(this, "请输入金额！", Toast.LENGTH_SHORT).show()
            return false
        } else if (mLastSelectItemPosition == -1) {
            Toast.makeText(this, "请选择类型！", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showDatePicker() {
        val dialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_date_pick, null)
        mDatePicker = view.findViewById(R.id.calendarView)
        mDatePicker?.setDateSelected(CalendarDay.today(), true)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        view.findViewById<Button>(R.id.determine_btn).setOnClickListener {
            val date = mDatePicker?.selectedDate?.date
            if (date != null) {
                if (date < Date()) {
                    mDateTv?.text = DateUtil.getDate("yyyy-MM-dd", date)
                } else {
                    mDateTv?.text = DateUtil.getCurrentDate("yyyy-MM-dd")
                    Toast.makeText(this, "无效时间！更正为当日${mDateTv?.text}", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onAddSuccess() {
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
        finish()
   //     startActivity(Intent(this@AddActivity, MainActivity::class.java))
    }

    override fun onAddFailure(msg: String) {
        Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateSuccess() {
        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onUpdateFailure(msg: String) {
        Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show()
    }

}
