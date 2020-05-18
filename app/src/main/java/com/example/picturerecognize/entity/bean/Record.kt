package com.example.picturerecognize.entity.bean

data class Record (var id : Int,
                   var userId : Int,
                   var type : String, // 支出 or 收入
                   var classify : String, // 分类
                   var amount : Float,
                   var date : String,
                   var note : String)