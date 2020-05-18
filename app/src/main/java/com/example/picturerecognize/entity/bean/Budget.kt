package com.example.picturerecognize.entity.bean

class Budget (var userId : Int,
              var classify : String, // 和record里的classify不同的是  少了支出类型 多了total总预算
              var month : String, // 某个月份 2020-04
              var budget : Float)