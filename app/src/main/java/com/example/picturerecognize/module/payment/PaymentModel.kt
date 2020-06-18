package com.example.picturerecognize.module.payment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.R
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.entity.view.payment.HeaderItem
import com.example.picturerecognize.entity.view.payment.LevelSecondItem
import com.example.picturerecognize.util.NumUtil

class PaymentModel(context : Context) {

    private var mDatabase : SQLiteDatabase?= null
    private var context = context.applicationContext

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    // 浮点数相加会有误差 如0.3+0.1并不等于0.4 而是0.4000000003 这里对累加结果直接简单的取整
    // 如果在这里修改需要修改多处 所以选则在adapter视图显示里去修改
    fun searchRecords(userId: Int, date : String, listener : PaymentPresenter.PaymentDataListener) {

        val sql = "select ${SQLiteHelper.FIELD_RECORD_ID}, ${SQLiteHelper.FIELD_TYPE}, ${SQLiteHelper.FIELD_CLASSIFY}, ${SQLiteHelper.FIELD_AMOUNT}, ${SQLiteHelper.FIELD_DATE}, ${SQLiteHelper.FIELD_NOTE} from ${SQLiteHelper.TABLE_RECORD} where ${SQLiteHelper.FIELD_USER_ID} = $userId and ${SQLiteHelper.FIELD_DATE} like '$date%' order by ${SQLiteHelper.FIELD_DATE} desc"
        val cursor = mDatabase?.rawQuery(sql, null)

        val expenseImageTypedArray = context.resources.obtainTypedArray(R.array.expense_type_image_color)
        val incomeImageTypedArray = context.resources.obtainTypedArray(R.array.income_type_image_color)
        val mExpenseTypeTextArray = context.resources.getStringArray(R.array.expense_type_text)
        val mIncomeTypeTextArray = context.resources.getStringArray(R.array.income_type_text)

        val data = mutableListOf<Any>()
        // 正在统计的那天的日期 总支出 总收入
        var currentTotalDay : String ?= null
        // 正在统计的那天 在data里的index 用于后面统计完了再修改前面的、
        var currentTotalHeaderIndex : Int = -1
        // 本月总支出总收入
        var monthlyExpense : Float = 0f
        var monthlyIncome : Float = 0f

        while (cursor?.moveToNext()!!) {
            val id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.FIELD_RECORD_ID))
            val type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_TYPE))
            val classify = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_CLASSIFY))
            val amount = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_AMOUNT))
            val date = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_DATE))
            val note = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_NOTE))

            if (currentTotalDay != date) {
                // 更改新header的位置 初始数据就先设置为0
                currentTotalHeaderIndex = data.size
                var header = HeaderItem(date, 0f, 0f)
                data.add(header)
            }
            // 添加数据
            var imageResId = -1
            var balance = ""
            if (type == "支出") {
                imageResId = expenseImageTypedArray.getResourceId(mExpenseTypeTextArray.indexOf(classify), -1)
                balance = "-$amount"
                // 修改header
                val prevItem = data[currentTotalHeaderIndex] as HeaderItem
                prevItem.expense += amount
                monthlyExpense += amount
            } else if (type == "收入") {
                imageResId = incomeImageTypedArray.getResourceId(mIncomeTypeTextArray.indexOf(classify), -1)
                balance = "+$amount"
                // 修改header
                val prevItem = data[currentTotalHeaderIndex] as HeaderItem
                prevItem.income += amount
                monthlyIncome += amount
            }
            val item = LevelSecondItem(id, note, imageResId, balance)
            data.add(item)
            currentTotalDay = date
        }
        expenseImageTypedArray.recycle()
        incomeImageTypedArray.recycle()
        cursor.close()

        listener.onGetData(data, NumUtil.keepOneDecimalPlace(monthlyExpense), NumUtil.keepOneDecimalPlace(monthlyIncome), NumUtil.keepOneDecimalPlace(monthlyIncome - monthlyExpense))
    }
}