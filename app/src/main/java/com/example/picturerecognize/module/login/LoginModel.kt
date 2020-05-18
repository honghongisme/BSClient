package com.example.picturerecognize.module.login

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.TABLE_USER
import com.example.picturerecognize.entity.bean.Budget
import com.example.picturerecognize.entity.bean.Record

class LoginModel(context : Context) {

    private var mDatabase : SQLiteDatabase?= null

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    fun syncData(records : MutableList<Record>, budgets : MutableList<Budget>) {
        syncRecordTable(records)
        syncBudgetTable(budgets)
    }

    private fun syncRecordTable(records : MutableList<Record>) {
        var sql = "delete from ${SQLiteHelper.TABLE_RECORD}"
        mDatabase?.execSQL(sql)
        mDatabase?.beginTransaction()
        for (record in records) {
            sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(${record.id}, ${record.userId}, '${record.type}', '${record.classify}', ${record.amount}, '${record.date}', '${record.note}')"
            mDatabase?.execSQL(sql)
        }
        mDatabase?.setTransactionSuccessful()
        mDatabase?.endTransaction()
    }

    private fun syncBudgetTable(budgets : MutableList<Budget>) {
        var sql = "delete from ${SQLiteHelper.TABLE_BUDGET}"
        mDatabase?.execSQL(sql)
        mDatabase?.beginTransaction()
        for (budget in budgets) {
            sql = "insert into ${SQLiteHelper.TABLE_BUDGET} values(${budget.userId}, '${budget.month}', '${budget.classify}', ${budget.budget})"
            mDatabase?.execSQL(sql)
        }
        mDatabase?.setTransactionSuccessful()
        mDatabase?.endTransaction()
    }

    fun insertUser(id : Int, account: String, password: String) {
        val sql = "insert into ${SQLiteHelper.TABLE_USER} values($id, '$account', '$password')"
        mDatabase?.execSQL(sql)
    }

    fun isExistUser(userId: Int) : Boolean {
        val sql = "select ${SQLiteHelper.FIELD_USER_ID} from $TABLE_USER where ${SQLiteHelper.FIELD_USER_ID} = '$userId'limit 1"
        val cursor = mDatabase?.rawQuery(sql, null)
        var id = -1
        if (cursor?.moveToNext()!!) {
            id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.FIELD_USER_ID))
        }
        return id != -1
    }
}