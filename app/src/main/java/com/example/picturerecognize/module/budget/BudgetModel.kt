package com.example.picturerecognize.module.budget

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.constans.Constants.CLASSIFY_MONTH_TOTAL
import com.example.picturerecognize.constans.Constants.EXPENSE
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.entity.view.BudgetItem
import com.example.picturerecognize.util.NumUtil

class BudgetModel(context : Context) {

    private var mDatabase : SQLiteDatabase?= null

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    fun searchClassifyBudget(userId : Int, month : String, listener : BudgetPresenter.BudgetListener) {
        val sql = "select ${SQLiteHelper.FIELD_BUDGET}, ${SQLiteHelper.FIELD_CLASSIFY} from ${SQLiteHelper.TABLE_BUDGET} where ${SQLiteHelper.FIELD_USER_ID} = $userId and ${SQLiteHelper.FIELD_MONTH} = '$month'"
        val cursor = mDatabase?.rawQuery(sql, null)
        var totalBudget = BudgetItem(CLASSIFY_MONTH_TOTAL, month, 0f, 0f, 0f, 0f)
        var data  = mutableListOf<BudgetItem>()
        while (cursor?.moveToNext()!!) {
            val classify = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_CLASSIFY))
            val budget = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_BUDGET))

            if (classify == CLASSIFY_MONTH_TOTAL) {
                val totalExpense = getTotalExpense(userId, month, null)
                var remainBudget = budget - totalExpense
                var proportion = 0f
                if (remainBudget < 0) {
                    remainBudget = 0f
                } else {
                    proportion = NumUtil.keepOneDecimalPlace(remainBudget / budget * 100)
                }
                totalBudget = BudgetItem(CLASSIFY_MONTH_TOTAL, month, budget, NumUtil.keepOneDecimalPlace(totalExpense), NumUtil.keepOneDecimalPlace(remainBudget), proportion)
            } else {
                val totalExpense = getTotalExpense(userId, month, classify)
                var remainBudget = budget - totalExpense
                var proportion = 0f
                if (remainBudget < 0) {
                    remainBudget = 0f
                } else {
                    proportion = NumUtil.keepOneDecimalPlace(remainBudget / budget * 100)
                }
                val item = BudgetItem(classify, month, budget, NumUtil.keepOneDecimalPlace(totalExpense), NumUtil.keepOneDecimalPlace(remainBudget), proportion)
                data.add(item)
            }
        }
        cursor.close()
        listener.onGetData(totalBudget, data)
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

    fun addBudget(userId: Int, month: String, classify: String, amount : Float) {
        val sql = "insert into ${SQLiteHelper.TABLE_BUDGET} values($userId, '$month', '$classify', $amount)"
        mDatabase?.execSQL(sql)
    }

    fun deleteBudget(userId: Int, month: String, classify: String) {
        val sql = "delete from ${SQLiteHelper.TABLE_BUDGET} where ${SQLiteHelper.FIELD_USER_ID} = $userId and ${SQLiteHelper.FIELD_CLASSIFY} = '$classify' and ${SQLiteHelper.FIELD_MONTH} = '$month'"
        mDatabase?.execSQL(sql)
    }
}