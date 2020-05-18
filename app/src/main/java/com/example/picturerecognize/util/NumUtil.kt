package com.example.picturerecognize.util

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object NumUtil {

    /**
     * 保留小数点后一位
     */
    fun keepOneDecimalPlace(num : Float) : Float {
        return formatNum("%.1f", num)
    }

    private fun formatNum(format : String, num : Float) : Float {
        return Formatter().format(format, num).toString().toFloat()
    }

    /**
     * 提取字符串里的数字成分
     * ￥280.5元 转换 280.5
     */
    fun getFloatFromStr(s : String) : Float {
        val p: Pattern = Pattern.compile("(\\d+\\.\\d+)")
        val m: Matcher = p.matcher(s)
        if (m.find()) {
            return m.group().toFloat()
        }
        return 0f
    }
}