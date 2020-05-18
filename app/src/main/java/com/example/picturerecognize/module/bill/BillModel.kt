package com.example.picturerecognize.module.bill

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.constans.Constants.EXPENSE
import com.example.picturerecognize.constans.Constants.INCOME
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.util.DateUtil
import com.example.picturerecognize.util.NumUtil

class BillModel(context : Context) {

    private var mDatabase : SQLiteDatabase?= null

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    // 根据当前月份创建数据个数 如：4月 数据就应该包括4月 3月 2月 1月 4组数据
    // 假设currentMonth = 2020-04  year = 2019  就得判断了 是12组数据
    fun searchBill (userId : Int, year : String, listener : BillPresenter.BillDataListener) {
        val data = mutableListOf<Bill>()
        if (year.toInt() > DateUtil.getCurrentYear()) listener.onGetData(data, 0f, 0f, 0f)
        else {
            var size = 12
            if (year.toInt() == DateUtil.getCurrentYear()) size = DateUtil.getCurrentMonth()
            for (i in 1..size) {
                val month = Bill("${i}月", 0f, 0f, 0f)
                data.add(month)
            }
            val sql = "select ${SQLiteHelper.FIELD_TYPE}, ${SQLiteHelper.FIELD_AMOUNT}, ${SQLiteHelper.FIELD_DATE} from ${SQLiteHelper.TABLE_RECORD} where ${SQLiteHelper.FIELD_USER_ID} = $userId and ${SQLiteHelper.FIELD_DATE} like '$year%' order by ${SQLiteHelper.FIELD_DATE} desc"
            val cursor = mDatabase?.rawQuery(sql, null)

            var income = 0f
            var expense = 0f
            var totalIncome = 0f
            var totalExpense = 0f
            var currentStatisticsMonth = -1 // 当前正统计到的月份

            while (cursor?.moveToNext()!!) {
                val type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_TYPE))
                val amount = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_AMOUNT))
                val date = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_DATE))

                val month = date.split("-")[1].toInt() // 2020-04-15 取出4

                if (currentStatisticsMonth == -1) {
                    currentStatisticsMonth = month
                }
                if (currentStatisticsMonth == month) {
                    if (type == EXPENSE) {
                        expense += amount
                    } else if (type == INCOME) {
                        income += amount
                    }
                } else { // 开始遇到下一个月份的第一条数据
                    // 修改上个月份的数据
                    val m = data[currentStatisticsMonth - 1]
                    m.expense = expense
                    m.income = income
                    m.balance = income - expense
                    totalIncome += income
                    totalExpense += expense
                    // 清空临时数据
                    expense = 0f
                    income = 0f
                    // 统计本条记录
                    if (type == EXPENSE) {
                        expense += amount
                    } else if (type == INCOME) {
                        income += amount
                    }
                    currentStatisticsMonth = month
                }
            }
            if (data.size != 0 && currentStatisticsMonth != -1) { // 加currentStatisticsMonth != -1条件 该年有可能没有数据 会导致下面数组越界
                // 修改还没来得及修改的最后一个月份
                val m = data[currentStatisticsMonth - 1]
                m.expense = expense
                m.income = income
                m.balance = income - expense

                totalIncome += income
                totalExpense += expense
            }
            cursor.close()
            listener.onGetData(data, NumUtil.keepOneDecimalPlace(totalIncome), NumUtil.keepOneDecimalPlace(totalExpense), NumUtil.keepOneDecimalPlace(totalIncome - totalExpense))
        }

    }

}