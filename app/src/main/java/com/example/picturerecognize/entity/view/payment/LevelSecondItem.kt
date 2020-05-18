package com.example.picturerecognize.entity.view.payment

data class LevelSecondItem (var id : Int,
                            var note : String,
                            var imageResId : Int, // 图片资源id
                            var balance : String // 金额 带正负
)