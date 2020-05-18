package com.example.picturerecognize.entity

data class FirstCommentItem (var userId : Int,
                             var commentId : Int,
                             var userAccount : String, // 用户名
                             var text : String,
                             var date : String)