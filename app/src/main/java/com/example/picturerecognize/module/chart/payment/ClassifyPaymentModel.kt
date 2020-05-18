package com.example.picturerecognize.module.chart.payment

import android.content.Context
import android.content.res.TypedArray
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.R
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.constans.Constants.EXPENSE
import com.example.picturerecognize.constans.Constants.INCOME
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.entity.view.payment.ClassifyHeaderItem
import com.example.picturerecognize.entity.view.payment.LevelSecondItem
import com.example.picturerecognize.util.DateUtil
import java.text.SimpleDateFormat
import java.util.*

class ClassifyPaymentModel(context : Context) {

    private var mDatabase : SQLiteDatabase?= null
    private var context = context.applicationContext

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    fun searchRecords(userId: Int, range: String, classify: String, type: String, listener: ClassifyPaymentPresenter.DataListener) {
        var dateCondition = ""
        when(range) {
            Constants.CURRENT_WEEK -> {
                val df = SimpleDateFormat("yyyy-MM-dd") //设置日期格式
                val cld: Calendar = Calendar.getInstance(Locale.CHINA)
                cld.firstDayOfWeek = Calendar.MONDAY //以周一为首日
                cld.timeInMillis = System.currentTimeMillis() //当前时间
                cld.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) //周一
                val mondy = df.format(cld.time)
                cld.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY) //周日
                val sunday = df.format(cld.time)
                dateCondition = "${SQLiteHelper.FIELD_DATE} >= '$mondy' and ${SQLiteHelper.FIELD_DATE} <= '$sunday'"
            }
            Constants.CURRENT_MONTH -> {
                val month = DateUtil.getCurrentDate("yyyy-MM")
                dateCondition = "${SQLiteHelper.FIELD_DATE} like '$month%'"
            }
            Constants.CURRENT_YEAR -> {
                val month = DateUtil.getCurrentDate("yyyy")
                dateCondition = "${SQLiteHelper.FIELD_DATE} like '$month%'"
            }
        }
        val sql = "select ${SQLiteHelper.FIELD_RECORD_ID}, ${SQLiteHelper.FIELD_AMOUNT}, ${SQLiteHelper.FIELD_DATE}, ${SQLiteHelper.FIELD_NOTE} from ${SQLiteHelper.TABLE_RECORD} where ${SQLiteHelper.FIELD_USER_ID} = $userId and $dateCondition and ${SQLiteHelper.FIELD_CLASSIFY} = '$classify' and ${SQLiteHelper.FIELD_TYPE} = '$type' order by ${SQLiteHelper.FIELD_DATE} desc"
        val cursor = mDatabase?.rawQuery(sql, null)

        var imageTypedArray : TypedArray? = null
        var textArray : Array<String>? = null
        if (type == EXPENSE) {
            imageTypedArray = context.resources.obtainTypedArray(R.array.expense_type_image_color)
            textArray = context.resources.getStringArray(R.array.expense_type_text)
        } else if (type == INCOME) {
            imageTypedArray = context.resources.obtainTypedArray(R.array.income_type_image_color)
            textArray = context.resources.getStringArray(R.array.income_type_text)
        }

        val data = mutableListOf<Any>()
        // 正在统计的那天的日期 总支出 总收入
        var currentTotalDay : String ?= null
        // 总金额
        var totalAmount = 0f


        while (cursor?.moveToNext()!!) {
            val id = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.FIELD_RECORD_ID))
            val amount = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_AMOUNT))
            val date = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_DATE))
            val note = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_NOTE))

            when (currentTotalDay) {
                null -> {
                    currentTotalDay = date
                    val header = ClassifyHeaderItem("${date.substring(8, 10)}日", date.substring(0, 7)) // 2020-02-03
                    data.add(header)
                    // 添加新header下的第一条数据
                    var imageResId = -1
                    val balance = "$amount"
                    totalAmount += amount
                    imageResId = imageTypedArray!!.getResourceId(textArray!!.indexOf(classify), -1)
                    val item = LevelSecondItem(id, note, imageResId, balance)
                    data.add(item)
                }
                date -> { // 统计
                    var imageResId = -1
                    val balance = "$amount"
                    totalAmount += amount
                    imageResId = imageTypedArray!!.getResourceId(textArray!!.indexOf(classify), -1)
                    val item = LevelSecondItem(id, note, imageResId, balance)
                    data.add(item)
                    currentTotalDay = date
                }
                else -> { // 新的一天
                    val header = ClassifyHeaderItem("${date.substring(8, 10)}日", date.substring(0, 7))
                    data.add(header)
                    // 添加新header下的第一条数据
                    var imageResId = -1
                    val balance = "$amount"
                    totalAmount += amount
                    imageResId = imageTypedArray!!.getResourceId(textArray!!.indexOf(classify), -1)
                    val item = LevelSecondItem(id, note, imageResId, balance)
                    data.add(item)

                    currentTotalDay = date
                }
            }
        }
        imageTypedArray?.recycle()
        cursor.close()

        listener.onGetData(data, totalAmount)
    }
}