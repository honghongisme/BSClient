package bean

data class CountData(var continuousClockDayNum : Int, // 已连续打卡多少天
var totalChargeDayNum : Int, // 记账总天数
var totalChargeNum : Int,  // 记账总笔数,
var isTodayClock : Boolean // 是否今天已打卡
                     )