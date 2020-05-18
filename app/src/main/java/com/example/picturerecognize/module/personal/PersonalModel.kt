package com.example.picturerecognize.module.personal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.constans.Constants.CLASSIFY_MONTH_TOTAL
import com.example.picturerecognize.constans.Constants.EXPENSE
import com.example.picturerecognize.constans.Constants.INCOME
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.entity.view.BudgetItem
import com.example.picturerecognize.util.NumUtil

class PersonalModel(context : Context) {

    private var mDatabase : SQLiteDatabase?= null

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    fun search(userId : Int, month : String, listener : PersonalPresenter.OnGetDataListener) {
        listener.onGetData(searchBill(userId, month), searchBudget(userId, month))
    }

    private fun searchBill(userId : Int, month : String) : Bill {
        var income = 0f
        var expense = 0f
        val sql = "select ${SQLiteHelper.FIELD_TYPE}, ${SQLiteHelper.FIELD_AMOUNT} from ${SQLiteHelper.TABLE_RECORD} where ${SQLiteHelper.FIELD_USER_ID} = $userId and ${SQLiteHelper.FIELD_DATE} like '$month%'"
        val cursor = mDatabase?.rawQuery(sql, null)
        while (cursor?.moveToNext()!!) {
            val type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_TYPE))
            val amount = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_AMOUNT))
            if (type == EXPENSE) {
                expense += amount
            } else if (type == INCOME) {
                income += amount
            }
        }
        cursor.close()
        return Bill(month.split("-")[1], NumUtil.keepOneDecimalPlace(income), NumUtil.keepOneDecimalPlace(expense), NumUtil.keepOneDecimalPlace(income - expense))
    }

    private fun searchBudget(userId: Int, month : String) : BudgetItem {
        val sql = "select ${SQLiteHelper.FIELD_BUDGET} from ${SQLiteHelper.TABLE_BUDGET} where ${SQLiteHelper.FIELD_USER_ID} = $userId and ${SQLiteHelper.FIELD_MONTH} = '$month' and ${SQLiteHelper.FIELD_CLASSIFY} = '$CLASSIFY_MONTH_TOTAL'"
        val cursor = mDatabase?.rawQuery(sql, null)
        var totalBudget = BudgetItem(CLASSIFY_MONTH_TOTAL, month, 0f, 0f, 0f, 0f)
        if (cursor?.moveToNext()!!) {
            val budget = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_BUDGET))
            val totalExpense = getTotalExpense(userId, month, null)
            var remainBudget = budget - totalExpense
            var proportion = 0f
            if (remainBudget < 0) {
                remainBudget = 0f
            } else {
                proportion = NumUtil.keepOneDecimalPlace(remainBudget / budget * 100)
            }
            totalBudget = BudgetItem(CLASSIFY_MONTH_TOTAL, month, budget, NumUtil.keepOneDecimalPlace(totalExpense), NumUtil.keepOneDecimalPlace(remainBudget), proportion)
        }
        cursor.close()
        return totalBudget
    }

    private fun getTotalExpense(userId: Int, month : String, classify : String?) : Float {
        var condition = ""
        if (classify != null) condition = "and ${SQLiteHelper.FIELD_CLASSIFY} = '$classify'"
        val sql = "select ${SQLiteHelper.FIELD_AMOUNT} from ${SQLiteHelper.TABLE_RECORD} where ${SQLiteHelper.FIELD_USER_ID} = $userId $condition and ${SQLiteHelper.FIELD_TYPE} = '$EXPENSE' and ${SQLiteHelper.FIELD_DATE} like '$month%' "
        val cursor = mDatabase?.rawQuery(sql, null)
        var totalAmount = 0f
        while (cursor?.moveToNext()!!) {
            val amount = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_AMOUNT))
            totalAmount += amount
        }
        cursor.close()
        return totalAmount
    }

    public fun clear() {
        deleteTable(SQLiteHelper.TABLE_BUDGET)
        deleteTable(SQLiteHelper.TABLE_RECORD)
        deleteTable(SQLiteHelper.TABLE_USER)
    }

    private fun deleteTable(tableName : String) {
        mDatabase?.execSQL("delete from $tableName")
    }
}