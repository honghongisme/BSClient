package com.example.picturerecognize.entity.view

data class RecordDetail (var imageResId : Int,
                         var classify : String, // 分类
                         var type : String, // 支出 or 收入
                         var amount : Float,
                         var date : String,
                         var note : String,
                         var id : Int)