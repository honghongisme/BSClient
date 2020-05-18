package com.example.picturerecognize.module.chart

import android.content.Context
import android.content.res.TypedArray
import android.database.sqlite.SQLiteDatabase
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.picturerecognize.R
import com.example.picturerecognize.constans.Constants.CURRENT_MONTH
import com.example.picturerecognize.constans.Constants.CURRENT_WEEK
import com.example.picturerecognize.constans.Constants.CURRENT_YEAR
import com.example.picturerecognize.constans.Constants.EXPENSE
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_TYPE
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_USER_ID
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.TABLE_RECORD
import com.example.picturerecognize.entity.view.chart.BarChartItem
import com.example.picturerecognize.entity.view.chart.PieChartItem
import com.example.picturerecognize.util.DateUtil
import com.example.picturerecognize.util.NumUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator


class ChartModel(context: Context) {

    private var mContext = context
    private var mDatabase : SQLiteDatabase ?= null
    private var mBarMaxWidth = 0

    init {
        mDatabase = SQLiteHelper(context).writableDatabase

        val screenWidth = getScreenWidth(context)
        mBarMaxWidth = screenWidth - 420
    }


    fun search(userId : Int, type : String, range : String, isShowBarChart : Boolean, listener: ChartPresenter.ChartDataListener) {
        var dateCondition = ""
        when(range) {
            CURRENT_WEEK -> {
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
            CURRENT_MONTH -> {
                val month = DateUtil.getCurrentDate("yyyy-MM")
                dateCondition = "${SQLiteHelper.FIELD_DATE} like '$month%'"
            }
            CURRENT_YEAR -> {
                val month = DateUtil.getCurrentDate("yyyy")
                dateCondition = "${SQLiteHelper.FIELD_DATE} like '$month%'"
            }
        }
        val sql = "select ${SQLiteHelper.FIELD_CLASSIFY}, ${SQLiteHelper.FIELD_AMOUNT} from $TABLE_RECORD where $FIELD_USER_ID = $userId and $FIELD_TYPE = '$type' and $dateCondition"
        val cursor = mDatabase?.rawQuery(sql, null)

        var totalAmount = 0f
        var map = mutableMapOf<String, Float>()
        val barChartData = mutableListOf<BarChartItem>()
        val pieChartData = mutableListOf<PieChartItem>()

        while (cursor?.moveToNext()!!) {
            val classify = cursor.getString(cursor.getColumnIndex(SQLiteHelper.FIELD_CLASSIFY))
            val amount = cursor.getFloat(cursor.getColumnIndex(SQLiteHelper.FIELD_AMOUNT))

            if (map.containsKey(classify)) {
                map[classify] = map[classify]?.plus(amount)!!
            } else {
                map[classify] = amount
            }
        }

        // 根据value排序
        map = map.toSortedMap(Comparator<String> { o1, o2 -> if (map[o1]!! < map[o2]!!) 1 else -1 })

        for (entry in map) {
            totalAmount += entry.value
        }
        if (isShowBarChart) { // 统计条形图(多了 图片 和 宽度)
            var imageTypedArray : TypedArray? = null
            var textArray : Array<String>? = null
            if (type == EXPENSE) {
                imageTypedArray = mContext.resources.obtainTypedArray(R.array.expense_type_image_color)
                textArray = mContext.resources.getStringArray(R.array.expense_type_text)
            } else {
                imageTypedArray = mContext.resources.obtainTypedArray(R.array.income_type_image_color)
                textArray = mContext.resources.getStringArray(R.array.income_type_text)
            }
            for (entry in map) {
                val imageResId = imageTypedArray.getResourceId(textArray.indexOf(entry.key), -1)
                val proportion = NumUtil.keepOneDecimalPlace(entry.value / totalAmount * 100)
                val barWidth = (mBarMaxWidth * proportion / 100).toInt()
                val item = BarChartItem(imageResId, entry.key, proportion, barWidth, NumUtil.keepOneDecimalPlace(entry.value))
                barChartData.add(item)
            }
            imageTypedArray.recycle()
            listener.onGetBarData(barChartData, NumUtil.keepOneDecimalPlace(totalAmount))
        } else { //统计饼图
            for (entry in map) {
                val proportion = NumUtil.keepOneDecimalPlace(entry.value / totalAmount * 100)
                val item = PieChartItem(entry.key, proportion, entry.value)
                pieChartData.add(item)
            }
            listener.onGetPieData(pieChartData)
        }
        cursor.close()
    }

    private fun getScreenWidth(context: Context) : Int {
        val manager: WindowManager = (context as ChartActivity).windowManager
        val outMetrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

}