package com.example.picturerecognize.module.detail

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.R
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.entity.view.RecordDetail

class DetailModel(context : Context) {

    private var mDatabase : SQLiteDatabase?= null
    private var context = context.applicationContext

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    fun searchRecord(id : Int) : RecordDetail? {
        val sql = "select ${SQLiteHelper.FIELD_TYPE}, ${SQLiteHelper.FIELD_CLASSIFY}, ${SQLiteHelper.FIELD_AMOUNT}, ${SQLiteHelper.FIELD_DATE}, ${SQLiteHelper.FIELD_NOTE} from ${SQLiteHelper.TABLE_RECORD} where ${SQLiteHelper.FIELD_RECORD_ID} = $id limit 1"
        val cursor = mDatabase?.rawQuery(sql, null)

        val expenseImageTypedArray = context.resources.obtainTypedArray(R.array.expense_type_image_grey)
        val incomeImageTypedArray = context.resources.obtainTypedArray(R.array.income_type_image_grey)
        val mExpenseTypeTextArray = context.resources.getStringArray(R.array.expense_type_text)
        val mIncomeTypeTextArray = context.resources.getStringArray(R.array.income_type_text)
        var detail : RecordDetail?= null

        while (cursor?.moveToNext()!!) {
            val type = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_TYPE))
            val classify = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_CLASSIFY))
            val amount = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_AMOUNT))
            val date = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_DATE))
            val note = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_NOTE))

            var imageResId = -1
            if (type == "支出") {
                imageResId = expenseImageTypedArray.getResourceId(mExpenseTypeTextArray.indexOf(classify), -1)

            } else if (type == "收入") {
                imageResId = incomeImageTypedArray.getResourceId(mIncomeTypeTextArray.indexOf(classify), -1)

            }

            detail = RecordDetail(imageResId, classify, type, amount, date, note, id)
        }
        expenseImageTypedArray.recycle()
        incomeImageTypedArray.recycle()
        cursor.close()

        return detail
    }

    fun delete(id : Int) {
        val sql = "delete from ${SQLiteHelper.TABLE_RECORD} where ${SQLiteHelper.FIELD_RECORD_ID} = $id"
        mDatabase?.execSQL(sql)
    }
}