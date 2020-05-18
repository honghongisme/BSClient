package com.example.picturerecognize.entity.view.chart

data class BarChartItem (var imageResId : Int,
                         var classify : String,
                         var proportion : Float,
                         var barWidth : Int,
                         var amount : Float)