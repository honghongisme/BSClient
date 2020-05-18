package com.example.picturerecognize.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    fun getCurrentDate(pattern: String) : String {
        return getDate(pattern, Date())
    }

    fun getDate(pattern: String, date: Date) : String {
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getCurrentYear() : Int {
        val cal = Calendar.getInstance()
        cal.time = Date()
        return cal.get(Calendar.YEAR)
    }

    fun getCurrentMonth() : Int {
        val cal = Calendar.getInstance()
        cal.time = Date()
        return cal.get(Calendar.MONTH) + 1
    }

    /**
     * 把 yyyy年MM月dd日 格式的时间转换为 yyyy-MM-dd 格式
     */
    fun getStandardDateStr(date : String) : String? {
        var format = SimpleDateFormat("yyyy年MM月dd日")
        return try {
            val d = format.parse(date)
            format = SimpleDateFormat("yyyy-MM-dd")
               format.format(d)
        } catch (e : ParseException) {
            println("日期解析错误！返回当前日期")
            format.format(Date())
        }
    }

}